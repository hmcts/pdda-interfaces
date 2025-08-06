package uk.gov.hmcts.framework.services.xsl;

public class UriResolver {
    private static final String FILE = "file://";
    private static final Character SLASH = '/';
    
    protected UriResolver() {
        super();
    }
    
    protected static String replaceDoubleBackslash(String urlString) {
        if (urlString != null && urlString.indexOf('\\') > -1) {
            return urlString.replace('\\', '/');
        }
        return urlString;
    }
    
    protected static boolean isAbsoluteUrl(String urlString, String base) {
        return urlString.startsWith("/") && (base.startsWith("jar") || base.startsWith("zip"));
    }
    
    protected static String getFile(String url) {
        return getFile(url, false);
    }
    
    protected static String getFile(String url, boolean additionalSlash) {
        StringBuilder sb = new StringBuilder();
        sb.append(FILE);
        if (additionalSlash) {
            sb.append(SLASH);
        }
        sb.append(url);
        return sb.toString();
    }
}
