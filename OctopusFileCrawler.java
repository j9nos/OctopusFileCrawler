import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

public class OctopusFileCrawler extends SimpleFileVisitor<Path> {

    private final Path sourceDirectory;
    private final Path targetDirectory;
    private final LinkedList<String> keywords;

    public OctopusFileCrawler(final String sourceDirectory, final String targetDirectory, final String... keywords) {
        this.sourceDirectory = Path.of(sourceDirectory);
        this.targetDirectory = Path.of(targetDirectory);
        this.keywords = new LinkedList<>(List.of(keywords));
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        try {
            if (containsKeywords(file)) {
                final Path destinationPath = targetDirectory.resolve(file.getFileName());
                Files.copy(file, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println(file + " is copied to " + destinationPath);
            }
        } catch (final Exception e) {
        }
        return FileVisitResult.CONTINUE;
    }

    public void crawl() throws IOException {
        Files.walkFileTree(sourceDirectory, this);
    }

    public boolean containsKeywords(final Path file) throws IOException {
        final LinkedList<String> copy = new LinkedList<>(keywords);
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                for (int i = copy.size() - 1; i >= 0; i--) {
                    if (line.toLowerCase().contains(copy.get(i).toLowerCase())) {
                        copy.remove(i);
                    }
                }
                if (copy.size() == 0) {
                    return true;
                }
            }
            return copy.size() == 0;
        }
    }

}
