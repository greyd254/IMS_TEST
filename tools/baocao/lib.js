// Ham dung chung cho bao cao: dinh dang Times New Roman 13, cach dong 1.5, bang, khoi ma, hinh, tieu de.
const fs = require('fs');
const D = require('docx');
const { Paragraph, TextRun, Table, TableRow, TableCell, ImageRun, AlignmentType, HeadingLevel, BorderStyle, WidthType,
  ShadingType, TableOfContents } = D;

const FONT = 'Times New Roman';
const CODE_FONT = 'Consolas';
const W_PORTRAIT = 9071; // 21cm - 3cm - 2cm (DXA)

let tblNo = 0, figNo = 0;

// ---------- van ban thuong (ho tro **dam** va `ma`) ----------
function runs(text, base = {}) {
  const out = [];
  const re = /(\*\*[^*]+\*\*|`[^`]+`)/g;
  let last = 0, m;
  while ((m = re.exec(text)) !== null) {
    if (m.index > last) out.push(new TextRun({ text: text.slice(last, m.index), font: FONT, size: 26, ...base }));
    const tok = m[0];
    if (tok.startsWith('**')) out.push(new TextRun({ text: tok.slice(2, -2), bold: true, font: FONT, size: 26, ...base }));
    else out.push(new TextRun({ text: tok.slice(1, -1), font: CODE_FONT, size: 22, ...base }));
    last = m.index + tok.length;
  }
  if (last < text.length) out.push(new TextRun({ text: text.slice(last), font: FONT, size: 26, ...base }));
  return out;
}

const P = (text, o = {}) => new Paragraph({
  alignment: o.align || AlignmentType.JUSTIFIED,
  spacing: { line: 360, after: o.after ?? 100 },
  indent: o.noIndent ? undefined : { firstLine: 567 },
  keepNext: o.keepNext,
  children: runs(text, o.run || {}),
});

const bullet = (text, level = 0) => new Paragraph({
  numbering: { reference: 'bullets', level },
  alignment: AlignmentType.JUSTIFIED,
  spacing: { line: 360, after: 40 },
  children: runs(text),
});

const H1 = (t) => new Paragraph({ heading: HeadingLevel.HEADING_1, pageBreakBefore: true, children: [new TextRun({ text: t, font: FONT })] });
const H2 = (t) => new Paragraph({ heading: HeadingLevel.HEADING_2, keepNext: true, children: [new TextRun({ text: t, font: FONT })] });
const H3 = (t) => new Paragraph({ heading: HeadingLevel.HEADING_3, keepNext: true, children: [new TextRun({ text: t, font: FONT })] });

// ---------- khoi ma / ket qua ----------
function code(lines, o = {}) {
  if (typeof lines === 'string') lines = lines.split('\n');
  return lines.map((l, i) => new Paragraph({
    spacing: { line: 240, after: 0, before: 0 },
    shading: { type: ShadingType.CLEAR, fill: o.fill || 'F2F2F2' },
    indent: { left: 60, right: 60 },
    keepLines: true,
    border: {
      ...(i === 0 ? { top: { style: BorderStyle.SINGLE, size: 4, color: 'BFBFBF', space: 2 } } : {}),
      ...(i === lines.length - 1 ? { bottom: { style: BorderStyle.SINGLE, size: 4, color: 'BFBFBF', space: 2 } } : {}),
      left: { style: BorderStyle.SINGLE, size: 12, color: o.bar || '2E75B6', space: 4 },
    },
    children: [new TextRun({ text: l.replace(/\t/g, '    ') || ' ', font: CODE_FONT, size: 19 })],
  })).concat([new Paragraph({ spacing: { after: 120 }, children: [] })]);
}

// ---------- bang ----------
const thin = { style: BorderStyle.SINGLE, size: 4, color: '7F7F7F' };
const borders = { top: thin, bottom: thin, left: thin, right: thin };

function cell(text, w, o = {}) {
  return new TableCell({
    width: { size: w, type: WidthType.DXA },
    borders,
    margins: { top: 50, bottom: 50, left: 90, right: 90 },
    shading: o.fill ? { type: ShadingType.CLEAR, fill: o.fill } : undefined,
    verticalAlign: 'center',
    children: String(text).split('\n').map(t => new Paragraph({
      alignment: o.align || AlignmentType.LEFT,
      spacing: { line: 250, after: 0 },
      children: [new TextRun({ text: t, font: o.mono ? CODE_FONT : FONT, size: o.size || 22, bold: !!o.bold, color: o.color })],
    })),
  });
}

function table(headers, rows, widths, o = {}) {
  const total = widths.reduce((a, b) => a + b, 0);
  const size = o.size || 22;
  const head = new TableRow({
    tableHeader: true, cantSplit: true,
    children: headers.map((h, i) => cell(h, widths[i], { fill: 'D9E2F3', bold: true, size, align: AlignmentType.CENTER })),
  });
  const body = rows.map(r => new TableRow({
    cantSplit: true,
    children: r.map((c, i) => cell(c ?? '', widths[i], { size, mono: (o.monoCols || []).includes(i), align: (o.centerCols || []).includes(i) ? AlignmentType.CENTER : AlignmentType.LEFT })),
  }));
  return new Table({ width: { size: total, type: WidthType.DXA }, columnWidths: widths, rows: [head, ...body] });
}

// bang ket qua truy van: tu chia do rong theo do dai noi dung
function resultTable(cols, rows) {
  const w = cols.map((c, i) => Math.max(6, Math.min(34, Math.max(c.length, ...rows.map(r => String(r[i] ?? '').length)))));
  const sum = w.reduce((a, b) => a + b, 0);
  const widths = w.map(x => Math.floor(W_PORTRAIT * x / sum));
  widths[widths.length - 1] += W_PORTRAIT - widths.reduce((a, b) => a + b, 0);
  const size = cols.length >= 7 ? 17 : cols.length >= 5 ? 19 : 21;
  return table(cols, rows, widths, { size });
}

const tblCaption = (t) => new Paragraph({ alignment: AlignmentType.CENTER, keepNext: true, spacing: { before: 120, after: 80 },
  children: [new TextRun({ text: `Bảng ${++tblNo}: ${t}`, bold: true, font: FONT, size: 24 })] });
const gap = () => new Paragraph({ spacing: { after: 120 }, children: [] });

// ---------- hinh ----------
function figure(file, widthPx, heightPx, caption) {
  return [
    new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 60, after: 60 }, keepNext: true,
      children: [new ImageRun({ type: 'png', data: fs.readFileSync(file), transformation: { width: widthPx, height: heightPx },
        altText: { title: caption, description: caption, name: 'hinh' } })] }),
    new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 160 },
      children: [new TextRun({ text: `Hình ${++figNo}: ${caption}`, italics: true, font: FONT, size: 24 })] }),
  ];
}

// khung giu cho anh chup man hinh: nguoi dung tu chen anh vao sau
function placeholder(mota) {
  const b = { style: BorderStyle.DASHED, size: 6, color: '7F7F7F', space: 8 };
  return [
    new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 120, after: 0, line: 480 }, keepNext: true,
      border: { top: b, bottom: b, left: b, right: b },
      shading: { type: ShadingType.CLEAR, fill: 'F7F7F7' },
      children: [new TextRun({ text: `[Chèn ảnh chụp màn hình: ${mota}]`, italics: true, color: '7F7F7F', font: FONT, size: 24 })] }),
    new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 80, after: 160 },
      children: [new TextRun({ text: `Hình ${++figNo}: ${mota}`, italics: true, font: FONT, size: 24 })] }),
  ];
}

// ---------- doc file nguon / log ----------
const readLines = (f) => fs.readFileSync(f, 'utf8').replace(/^﻿/, '').split(/\r?\n/);

/** Lay khoi tu dong khop `start` den dong dau tien thoa `endTest` (bao gom hoac khong). */
function extract(file, start, endTest, includeEnd = true) {
  const L = readLines(file);
  const i = L.findIndex(l => start.test(l));
  if (i < 0) throw new Error('Khong tim thay ' + start + ' trong ' + file);
  const out = [];
  for (let j = i; j < L.length; j++) {
    if (j > i && endTest(L[j])) { if (includeEnd) out.push(L[j]); break; }
    out.push(L[j]);
  }
  return out;
}

/** Log SQL*Plus: bo dong trong, dong SET/SPOOL/COLUMN; cat khoang trang cuoi. */
function cleanLog(file) {
  return readLines(file).map(l => l.replace(/\s+$/, '')).filter(l => l.trim() !== '' && !/^SQL> (SET|SPOOL|COLUMN|ALTER SESSION SET CONTAINER)/.test(l)
    && !/^(Help: https|SQL> $)/.test(l));
}
/** Cac phan cua log chia theo dong "=== n. ...". */
function logSections(file) {
  const secs = {};
  let cur = null;
  for (const l of cleanLog(file)) {
    const m = l.match(/^(?:SQL> PROMPT )?=== (\d+[a-z]?)\./);
    if (m) { cur = m[1]; secs[cur] = []; if (l.startsWith('SQL> PROMPT')) continue; }
    if (cur) secs[cur].push(l);
  }
  return secs;
}

module.exports = { D, FONT, CODE_FONT, W_PORTRAIT, P, bullet, H1, H2, H3, code, table, resultTable, tblCaption, gap, figure,
  placeholder, readLines, extract, cleanLog, logSections, runs };
