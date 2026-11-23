# E-Commerce_System

# E-Commerce System (OOP Assignment)

## 1. Môi trường & Thư viện
- Java SE 17 trở lên.
- Thư viện được phép: `java.util.*`, `java.io.*`, `java.time.*`, `org.json.*`, `javafx.*`.
- Chạy chương trình từ file `src/Main.java`.

## 2. Quy tắc bắt buộc
- Chỉ sử dụng đường dẫn tương đối (`data/users.txt`, `data/products.txt`, `data/orders.txt`, `data/figure/`).
- Chỉ duy nhất lớp `IOInterface` được chứa `System.out.println()` và `Scanner`.
- Không push file `.zip` lên Git.

## 3. Quy trình Git cho thành viên
1. Lấy code mới nhất từ `main` trước khi code:
   `git checkout main && git pull origin main`
2. Chuyển sang nhánh của mình và cập nhật:
   `git checkout <ten-nhanh> && git merge main`
3. Commit thường xuyên (2-3 lần/ngày) và push:
   `git add .`
   `git commit -m "feat: mo ta ngan gon"`
   `git push origin <ten-nhanh>`
4. Tạo Pull Request (PR) trên GitHub để Lead review và merge vào `main`.