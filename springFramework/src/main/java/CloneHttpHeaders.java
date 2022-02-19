import org.springframework.http.MediaType;
import org.springframework.util.*;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CloneHttpHeaders implements MultiValueMap<String, String>, Serializable {

    private static final long serialVersionUID = -8578554704772377436L;

    public static final String ACCEPT = "Accept";

    public static final String ACCEPT_CHARSET = "Accept-Charset";

    public static final String ACCEPT_ENCODING = "Accept-Encoding";

    public static final String ACCEPT_LANGUAGE = "Accept-Language";

    public static final String ACCEPT_PATCH = "Accept-Patch";

    public static final String ACCEPT_RANGES = "Accept-Ranges";

    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";

    public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";

    public static final String AGE = "Age";

    public static final String ALLOW = "Allow";

    public static final String AUTHORIZATION = "Authorization";

    public static final String CACHE_CONTROL = "Cache-Control";

    public static final String CONNECTION = "Connection";

    public static final String CONTENT_ENCODING = "Content-Encoding";

    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    public static final String CONTENT_LANGUAGE = "Content-Language";

    public static final String CONTENT_LOCATION = "Content-Location";

    public static final String CONTENT_RANGE = "Content-Range";

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String COOKIE = "Cookie";

    public static final String DATE = "Date";

    public static final String ETAG = "ETag";

    public static final String EXPECT = "Expect";

    public static final String EXPIRES = "Expires";

    public static final String FROM = "From";

    public static final String HOST = "Host";

    public static final String IF_MATCH = "If-Match";

    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

    public static final String IF_NONE_MATCH = "If-None-Match";

    public static final String IF_RANGE = "If-Range";

    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

    public static final String LAST_MODIFIED = "Last-Modified";

    public static final String LINK = "Link";

    public static final String LOCATION = "Location";

    public static final String MAX_FORWARDS = "Max-Forwards";

    public static final String ORIGIN = "Origin";

    public static final String PRAGMA = "Pragma";

    public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";

    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";

    public static final String RANGE = "Range";

    public static final String REFERER = "Referer";

    public static final String RETRY_AFTER = "Retry-After";

    public static final String SERVER = "Server";

    public static final String SET_COOKIE = "Set-Cookie";

    public static final String SET_COOKIE2 = "Set-Cookie2";

    public static final String TE = "TE";

    public static final String TRAILER = "Trailer";

    public static final String TRANSFER_ENCODING = "Transfer-Encoding";

    public static final String UPGRADE = "Upgrade";

    public static final String USER_AGENT = "User-Agent";

    public static final String VARY = "Vary";

    public static final String VIA = "Via";

    public static final String WARNING = "Warning";

    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    public static final CloneHttpHeaders EMPTY = new ReadOnlyHttpHeaders(new LinkedMultiValueMap<>());

    private static final Pattern ETAG_HEADER_VALUE_PATTERN = Pattern.compile("\\*|\\s*((W\\/)?(\"[^\"]*\"))\\s*,?");

    private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Locale.ENGLISH);

    private static final ZoneId GMT = ZoneId.of("GMT");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).withZone(GMT);

    private static final DateTimeFormatter[] DATE_PARSERS = new DateTimeFormatter[] {
            DateTimeFormatter.RFC_1123_DATE_TIME,
            DateTimeFormatter.ofPattern("EEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", Locale.US).withZone(GMT)
    };


    final MultiValueMap<String, String> headers;


    public CloneHttpHeaders() {
        this(CollectionUtils.toMultiValueMap(new LinkedCaseInsensitiveMap<>(8, Locale.ENGLISH)));
    }

    public CloneHttpHeaders(MultiValueMap<String, String> headers){
        Assert.notNull(headers, "MultiValueMap must not be null");
        this.headers = headers;
    }

    public List<String> getOrEmpty(Object headerName){
        List<String> values = get(headerName);
        return (values != null ? values : Collections.emptyList());
    }

    public void setAccept(List<MediaType> acceptableMediaTypes){
        set(ACCEPT, MediaType.toString(acceptableMediaTypes));
    }

    public List<MediaType> getAccept(){
        return MediaType.parseMediaTypes(get(ACCEPT));
    }

    public void setAcceptLanguage(List<Locale.LanguageRange> languages){
        Assert.notNull(languages, "LanguageRange List must not be null");
        DecimalFormat decimal = new DecimalFormat("0.0", DECIMAL_FORMAT_SYMBOLS);
        List<String> values = languages.stream()
                .map(range ->
                        range.getWeight() == Locale.LanguageRange.MAX_WEIGHT ?
                                range.getRange() :
                                range.getRange() + ";q=" + decimal.format(range.getWeight()))
                .collect(Collectors.toList());
        set(ACCEPT_LANGUAGE, toCommaDelimitedString(values));
    }

    @Override
    public String getFirst(String key) {
        return null;
    }

    @Override
    public void add(String key, String value) {

    }

    @Override
    public void addAll(String key, List<? extends String> values) {

    }

    @Override
    public void addAll(MultiValueMap<String, String> values) {

    }

    @Override
    public void set(String key, String value) {

    }

    @Override
    public void setAll(Map<String, String> values) {

    }

    @Override
    public Map<String, String> toSingleValueMap() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public List<String> get(Object key) {
        return null;
    }

    @Override
    public List<String> put(String key, List<String> value) {
        return null;
    }

    @Override
    public List<String> remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<List<String>> values() {
        return null;
    }

    @Override
    public Set<Entry<String, List<String>>> entrySet() {
        return null;
    }
}
