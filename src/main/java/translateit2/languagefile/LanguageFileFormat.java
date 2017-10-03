package translateit2.languagefile;

public enum LanguageFileFormat {
    // note [MD] (3) DEFAULT is not used - and not surprisingly perhaps? There could be a place that specifies that
    //   the default format is either PO or PROPERTIES or XLIFF. But can a file be in "default" format?
    DEFAULT, PO, PROPERTIES, XLIFF;
}
