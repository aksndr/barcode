package ss;

/**
 * @author a.polyvyanyy
 * refactored by: a.arzamastsev
 */

import com.google.common.collect.Lists;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;

public class Barcode {

    private static final EnumMap<DecodeHintType, Object> HINTS;
    private static final EnumMap<DecodeHintType, Object> HINTS_PURE;

    static {
        HINTS = new EnumMap<>(DecodeHintType.class);
        HINTS.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        HINTS.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.of(BarcodeFormat.CODE_128));
//        HINTS.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
        HINTS_PURE = new EnumMap<>(HINTS);
        HINTS_PURE.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            String x = decodeFile(args[0]);
            System.out.println(x);
        }
    }

    private static String decodeFile(String filePath) {
        if (filePath == null || filePath.length() == 0 ) {
            return "ERROR. File path is not received";
        }
        FileInputStream fileInputStream;

        File file = new File(filePath);

        byte[] bFile = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();

        }catch(Exception e){
            return e.getMessage();
        }
        return decode(bFile);
    }

    public static String decode(byte[] bytes) {
        if (bytes == null || bytes.length == 0 ) {
            return "ERROR. Zero bytes received";
        }
        try {

            InputStream is = new ByteArrayInputStream(bytes);


            BufferedImage image = ImageIO.read(is);
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
            Collection<Result> results = Lists.newArrayListWithCapacity(1);
            Reader reader = new MultiFormatReader();
            Result[] theResults;
            // Look for multiple barcodes
            MultipleBarcodeReader multiReader = new GenericMultipleBarcodeReader(reader);
            try {
                theResults = multiReader.decodeMultiple(bitmap, HINTS);
                if (theResults != null) {
                    results.addAll(Arrays.asList(theResults));
                }
            } catch (NotFoundException nfe) {
                //ignore
            }

            if (results.isEmpty()) {
                // Look for pure barcode
                Result theResult = reader.decode(bitmap, HINTS_PURE);
                if (theResult != null) {
                    results.add(theResult);
                }
            }

            if (results.isEmpty()) {
                // Look for normal barcode in photo
                Result theResult = reader.decode(bitmap, HINTS);
                if (theResult != null) {
                    results.add(theResult);
                }
            }

            if (results.isEmpty()) {
                // Try again with other binarizer
                BinaryBitmap hybridBitmap = new BinaryBitmap(new HybridBinarizer(source));
                Result theResult = reader.decode(hybridBitmap, HINTS);
                if (theResult != null) {
                    results.add(theResult);
                }
            }
            return StringUtils.collectionToCommaDelimitedString(results);

        } catch (ReaderException re) {
            return "ERROR. Exception while reading barcode. " + re.getMessage();
        } catch (Exception ex) {
            return "ERROR. General exception! " + ex.getMessage();
        }
    }
}
