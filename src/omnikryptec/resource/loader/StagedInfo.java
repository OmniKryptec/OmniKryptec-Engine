package omnikryptec.resource.loader;

import de.codemakers.io.file.AdvancedFile;

/**
 * StagedInfo
 *
 * @author Panzer1119
 */
public class StagedInfo {

    private final long options;
    private final AdvancedFile file;

    public StagedInfo(long options, AdvancedFile file) {
        this.options = options;
        this.file = file;
    }

    public final long getOptions() {
        return options;
    }

    public final AdvancedFile getFile() {
        return file;
    }

    public final boolean isLoadingXMLInfo() {
        return (options | ResourceLoader.LOAD_XML_INFO) == options;
    }

}
