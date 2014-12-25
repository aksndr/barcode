import org.junit.Test;
import ss.Barcode;

import java.io.File;
import java.io.FileInputStream;

/**
 * User: a.arzamastsev Date: 23.12.2014 Time: 11:41
 */
public class BarcodeTest {
    @Test
    public void testBcode(){
    String testFilePath = "C:\\Workspace\\barcode\\main\\java\\resources\\BROTHER_7860DW_Of_525_004166.jpg";
        Barcode.main(new String[]{testFilePath});

    }

    @Test
    public void testBcodeBytes(){
        String testFilePath = "C:\\Workspace\\barcode\\main\\java\\resources\\BROTHER_7860DW_Of_525_004166.jpg";
        FileInputStream fileInputStream;

        File file = new File(testFilePath);

        byte[] bFile = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        String res = Barcode.decode(bFile);
        System.out.print(res);

    }

}
