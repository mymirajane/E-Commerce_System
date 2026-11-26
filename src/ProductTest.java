import model.ProductListResult;
import operation.ProductOperation;

public class ProductTest {
    public static void main(String[] args) {
        ProductOperation op = ProductOperation.getInstance();
        op.extractProductsFromFiles();


        ProductListResult page1 = op.getProductList(1);
        System.out.println("Trang 1 so luong: " + page1.getProductList().size() + " / Tong trang: " + page1.getTotalPages());


        System.out.println("Tim theo ID p001: " + op.getProductById("p001"));
        System.out.println("Tim tu khoa 'phone': " + op.getProductListByKeyword("phone").size() + " ket qua");


        boolean deleted = op.deleteProduct("p001");
        System.out.println("Xoa p001 thanh cong: " + deleted);
        System.out.println("Tim lai p001 sau khi xoa: " + op.getProductById("p001"));

        op.generateCategoryFigure();
        op.generateDiscountFigure();
        op.generateLikesCountFigure();
        op.generateDiscountLikesCountFigure();
        System.out.println("Da xuat 4 bieu do vao thu muc data/figure!");
    System.exit(0);
    }
}