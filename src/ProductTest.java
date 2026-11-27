import io.IOInterface;
import model.ProductListResult;
import operation.ProductOperation;

public class ProductTest {
    public static void main(String[] args) {
        IOInterface io = IOInterface.getInstance();
        ProductOperation op = ProductOperation.getInstance();
        op.extractProductsFromFiles();


        ProductListResult page1 = op.getProductList(1);
        io.printMessage("Trang 1 so luong: " + page1.getProductList().size() + " / Tong trang: " + page1.getTotalPages());


        io.printMessage("Tim theo ID p001: " + op.getProductById("p001"));
        io.printMessage("Tim tu khoa 'phone': " + op.getProductListByKeyword("phone").size() + " ket qua");


        boolean deleted = op.deleteProduct("p001");
        io.printMessage("Xoa p001 thanh cong: " + deleted);
        io.printMessage("Tim lai p001 sau khi xoa: " + op.getProductById("p001"));

        op.generateCategoryFigure();
        op.generateDiscountFigure();
        op.generateLikesCountFigure();
        op.generateDiscountLikesCountFigure();
        io.printMessage("Da xuat 4 bieu do vao thu muc data/figure!");
    }
}
