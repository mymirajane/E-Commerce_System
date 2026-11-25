import model.ProductListResult;
import operation.ProductOperation;

public class ProductTest {
    public static void main(String[] args) {
        ProductOperation op = ProductOperation.getInstance();
        op.extractProductsFromFiles();

        // 1. Kiểm tra phân trang (10 sản phẩm/trang)
        ProductListResult page1 = op.getProductList(1);
        System.out.println("Trang 1 so luong: " + page1.getProductList().size() + " / Tong trang: " + page1.getTotalPages());

        // 2. Kiểm tra tìm theo ID & từ khóa
        System.out.println("Tim theo ID p001: " + op.getProductById("p001"));
        System.out.println("Tim tu khoa 'phone': " + op.getProductListByKeyword("phone").size() + " ket qua");

        // 3. Kiểm tra xóa 1 sản phẩm
        boolean deleted = op.deleteProduct("p001");
        System.out.println("Xoa p001 thanh cong: " + deleted);
        System.out.println("Tim lai p001 sau khi xoa: " + op.getProductById("p001"));
        // 4. Kiểm tra xuất 4 biểu đồ ra thư mục data/figure/
        op.generateCategoryFigure();
        op.generateDiscountFigure();
        op.generateLikesCountFigure();
        op.generateDiscountLikesCountFigure();
        System.out.println("Da xuat 4 bieu do vao thu muc data/figure!");
    System.exit(0);
    }
}