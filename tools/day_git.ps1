# Đẩy đúng các file cần chia sẻ lên GitHub (danh sách trắng: chỉ file liệt kê dưới đây mới được thêm).
# Chạy:  powershell -ExecutionPolicy Bypass -File tools\day_git.ps1
#        thêm -Yes để bỏ qua bước hỏi xác nhận, -Message "nội dung" để đặt sẵn thông điệp commit.
param(
    [string]$Remote  = "https://github.com/greyd254/IMS_TEST.git",
    [string]$Branch  = "main",
    [string]$Message = "",
    [switch]$Yes
)

# "Continue": PowerShell 5.1 coi thông báo thường của git (stderr) là lỗi nên không dùng "Stop"; tự kiểm tra $LASTEXITCODE.
$ErrorActionPreference = "Continue"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

# --- Những gì được đẩy (đường dẫn tương đối từ thư mục gốc dự án) ---
$include = @(
    "sql",
    "web/src",
    "web/pom.xml",
    "docs",
    "tools/baocao/build.js",
    "tools/baocao/lib.js",
    "tools/baocao/XuatDuLieu.java",
    "tools/baocao/cap_nhat_muc_luc.ps1",
    "tools/baocao/dulieu.json",
    "tools/day_git.ps1",
    "README.md",
    "TIEN_DO.md",
    "SO_DO_QUAN_HE.md",
    ".gitignore"
)

# --- .gitignore: bổ sung các dòng còn thiếu (không xóa nội dung cũ) ---
$ignoreNeeded = @('target/', '.idea/', '*.iml', 'node_modules/', '~$*')
$gi = Join-Path $root ".gitignore"
$cur = if (Test-Path $gi) { Get-Content $gi -Encoding UTF8 } else { @() }
$add = $ignoreNeeded | Where-Object { $cur -notcontains $_ }
if ($add) {
    Add-Content $gi -Value ("", "### Bổ sung bởi day_git.ps1 ###") -Encoding UTF8
    Add-Content $gi -Value $add -Encoding UTF8
    Write-Host "Đã bổ sung .gitignore: $($add -join ', ')"
}

# --- Khởi tạo repo và remote ---
if (-not (Test-Path (Join-Path $root ".git"))) {
    git init | Out-Null
    Write-Host "Đã git init."
}
git checkout -B $Branch | Out-Null
$has = git remote
if ($has -contains "origin") { git remote set-url origin $Remote } else { git remote add origin $Remote }

# --- Chỉ thêm các đường dẫn trong danh sách ---
foreach ($p in $include) {
    if (Test-Path (Join-Path $root $p)) { git add -- $p }
    else { Write-Host "Bỏ qua (không tồn tại): $p" -ForegroundColor Yellow }
}

Write-Host "`n=== Các file sẽ được commit ===" -ForegroundColor Cyan
git status --short
$staged = git diff --cached --name-only
if (-not $staged) { Write-Host "Không có thay đổi mới để đẩy."; exit 0 }

# Cảnh báo nếu lọt file không mong muốn
$bad = $staged | Where-Object { $_ -match '(^|/)(target|node_modules|\.idea)/' -or $_ -match '\.(dmp|dbf|ctl|jar|iml)$' }
if ($bad) {
    Write-Host "`nCẢNH BÁO: có file không nên đẩy:" -ForegroundColor Red
    $bad | ForEach-Object { Write-Host "  $_" }
    Write-Host "Dừng lại. Chạy 'git reset' rồi kiểm tra .gitignore."
    exit 1
}

if (-not $Yes) {
    $ans = Read-Host "`nĐẩy lên $Remote (nhánh $Branch)? [y/N]"
    if ($ans -notmatch '^(y|Y)') { Write-Host "Đã hủy (các file vẫn ở trạng thái staged)."; exit 0 }
}

if (-not $Message) {
    $def = "Cap nhat " + (Get-Date -Format "dd/MM/yyyy HH:mm")
    $in = Read-Host "Thông điệp commit [$def]"
    $Message = if ($in) { $in } else { $def }
}

git commit -m $Message
if ($LASTEXITCODE -ne 0) { Write-Host "Commit thất bại." -ForegroundColor Red; exit 1 }
git push -u origin $Branch
if ($LASTEXITCODE -ne 0) { Write-Host "Push thất bại (kiểm tra repo đã tạo trên GitHub và đã đăng nhập chưa)." -ForegroundColor Red; exit 1 }
Write-Host "`nXong: https://github.com/greyd254/IMS_TEST" -ForegroundColor Green
