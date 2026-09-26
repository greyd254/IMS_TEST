# Mo file Word bang Microsoft Word (COM), cap nhat muc luc, dem trang, luu lai va xuat PDF.
# Dung sau khi chay: node build.js   (build.js ghi ra docs\BaoCao_Oracle_QuanLySuCo.docx)
$src = 'C:\Users\GreyD\IdeaProjects\IMS\docs\BaoCao_Oracle_QuanLySuCo.docx'
$pdf = Join-Path $PSScriptRoot 'baocao.pdf'
$w = New-Object -ComObject Word.Application
$w.Visible = $false; $w.DisplayAlerts = 0
try {
  $d = $w.Documents.Open($src)
  $d.Repaginate()
  if ($d.TablesOfContents.Count -gt 0) { $d.TablesOfContents.Item(1).Update() }
  $d.Fields.Update() | Out-Null
  $d.Repaginate()
  "So trang: " + $d.ComputeStatistics(2)
  $d.SaveAs2($src, 16)
  $d.ExportAsFixedFormat($pdf, 17)
  $d.Close(0)
} finally { $w.Quit() }
