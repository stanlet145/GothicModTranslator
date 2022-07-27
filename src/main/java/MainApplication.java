import service.files.FileProcessingServiceImpl;
import service.write.FileWriterImpl;

public class MainApplication {
    public static void main(String[] args) {

        var writer = new FileWriterImpl();
        writer.writeBetweenFiles("en/DIA_VLK_412_Harad.d", "pl/DIA_VLK_412_Harad.d");

        var processor = new FileProcessingServiceImpl();
        processor.mapProcessedFiles();
    }
}
