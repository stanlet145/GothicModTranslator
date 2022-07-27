import service.files.FileProcessingServiceImpl;

public class MainApplication {
    public static void main(String[] args) {
        var processor = new FileProcessingServiceImpl();
        processor.processFiles();
    }
}
