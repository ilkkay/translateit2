package translateit2.exception;

public enum TranslateIt2ErrorCode {
    CANNOT_CREATE_UPLOAD_DIRECTORY(""),
    CANNOT_CREATE_DOWNLOAD_DIRECTORY(""),
    CANNOT_CREATE_ROOT_DIRECTORY(""),
    CANNOT_CREATE_PERMANENT_DIRECTORY(""),
    CANNOT_CREATE_FILE(""),
    CANNOT_MOVE_FILE(""),
    CANNOT_READ_LANGUAGE_FROM_FILE_NAME("FileStorageService.code_missing"),
    CANNOT_READ_APPLICATION_NAME_FROM_FILE_NAME( ""),
    CANNOT_READ_FILE("FileStorageService.not_read_properties_file"),
    CANNOT_UPLOAD_FILE(""),
    FILE_NOT_FOUND( "FileStorageService.not_find_file"),
    FILE_TOBELOADED_IS_EMPTY("FileStorageService.empty_properties_file"),
    IMPROPER_CHARACTERSET_IN_FILE("FileStorageService.false_encoding"),
    IMPROPER_EXTENSION_IN_FILE_NAME("FileStorageService.not_properties_file"),
    IMPROPER_APPLICATION_NAME_IN_FILE_NAME(""),
    IMPROPER_LOCALE_IN_FILE_NAME(""),
    IMPROPER_IDENTIFIER_IN_DATA_OBJECT(""),
    UNDEFINED_ERROR("");

    private final String description;

    private TranslateIt2ErrorCode(String description) {
      this.description = description;
    }

    public String getDescription() {
       return description;
    }
  }