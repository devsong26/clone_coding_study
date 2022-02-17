import org.springframework.lang.Nullable;

/**
 * HttpStatusCode가 이렇게 많을 줄 몰랐다.
 *
 * @Deprecated는 왜 사용하는걸까?
 * 사용을 권장하지 않는 클래스나 메소드등에 사용한다.
 * 사용 불가능은 아니나 나중에 없어질 수도 있는 대상으로 분류된다.
 *
 * SRP와 관심사 분리를 생각하면서 코드를 바라보면
 * 클래스의 멤버만을 대상으로 하는 밸리데이션, resolve로직, build()등을 만드는 것만 확인했다.
 *
 * 나중에 더 차용할 기능이 있다면 공부해두자.
 *
 */
public enum CloneHttpStatus {

    // 1xx informational

    CONTINUE(100, Series.INFORMATIONAL, "Continue"),
    SWITCHING_PROTOCOLS(101, Series.INFORMATIONAL, "Switching Protocols"),
    PROCESSING(102, Series.INFORMATIONAL, "Processing"),
    CHECKPOINT(103, Series.INFORMATIONAL, "Checkpoint"),

    // 2xx Success
    OK(200, Series.SUCCESSFUL, "OK"),
    CREATED(201, Series.SUCCESSFUL, "Created"),
    ACCEPTED(202, Series.SUCCESSFUL, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, Series.SUCCESSFUL, "Non-Authoritative Information"),
    NO_CONTENT(204, Series.SUCCESSFUL, "No Content"),
    RESET_CONTENT(205, Series.SUCCESSFUL, "Reset Content"),
    PARTIAL_CONTENT(206, Series.SUCCESSFUL, "Partial Content"),
    MULTI_STATUS(207, Series.SUCCESSFUL, "Multi-Status"),
    ALREADY_REPORTED(208, Series.SUCCESSFUL, "Already Reported"),
    IM_USED(226, Series.SUCCESSFUL, "IM Used"),

    // 3xx Redirection
    MULTIPLE_CHOICES(300, Series.REDIRECTION, "Multiple Choices"),
    MOVED_PERMANENTLY(301, Series.REDIRECTION, "Moved Permanently"),
    FOUND(302, Series.REDIRECTION, "Found"),

    // found로 대체됨
    @Deprecated
    MOVED_TEMPORARILY(302, Series.REDIRECTION, "Moved Temporarily"),

    SEE_OTHER(303, Series.REDIRECTION, "See Other"),
    NOT_MODIFIED(304, Series.REDIRECTION, "Not Modified"),

    @Deprecated
    USE_PROXY(305, Series.REDIRECTION, "Use Proxy"),

    TEMPORARY_REDIRECT(307, Series.REDIRECTION, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, Series.REDIRECTION, "Permanent Redirect"),

    // 4xx Client Error
    BAD_REQUEST(400, Series.CLIENT_ERROR, "Bad Request"),
    UNAUTHORIZED(401, Series.CLIENT_ERROR, "Unauthorized"),
    PAYMENT_REQUIRED(402, Series.CLIENT_ERROR, "Payment Required"),
    FORBIDDEN(403, Series.CLIENT_ERROR, "Forbidden"),
    NOT_FOUND(404, Series.CLIENT_ERROR, "Not Found"),
    METHOD_NOT_ALLOWED(405, Series.CLIENT_ERROR, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, Series.CLIENT_ERROR, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, Series.CLIENT_ERROR, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, Series.CLIENT_ERROR, "Request Timeout"),
    CONFLICT(409, Series.CLIENT_ERROR, "Conflict"),
    GONE(410, Series.CLIENT_ERROR, "Gone"),
    LENGTH_REQUIRED(411, Series.CLIENT_ERROR, "Length Required"),
    PRECONDITION_FAILED(412, Series.CLIENT_ERROR, "Precondition Failed"),
    PAYLOAD_TOO_LARGE(413, Series.CLIENT_ERROR, "Payload Too Large"),

    @Deprecated
    REQUEST_ENTITY_TOO_LARGE(413, Series.CLIENT_ERROR, "Request Entity Too Large"),

    URI_TOO_LONG(414, Series.CLIENT_ERROR, "URI Too Long"),

    @Deprecated
    REQUEST_URI_TOO_LONG(414, Series.CLIENT_ERROR, "Request-URI Too Long"),

    UNSUPPORTED_MEDIA_TYPE(415, Series.CLIENT_ERROR, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, Series.CLIENT_ERROR, "Request range not satisfiable"),
    EXPECTATION_FAILED(417, Series.CLIENT_ERROR, "Expectation Failed"),
    I_AM_A_TEAPOT(418, Series.CLIENT_ERROR, "I'm a teapot"),

    @Deprecated
    INSUFFICIENT_SPACE_ON_RESOURCE(419, Series.CLIENT_ERROR, "Insufficient Space On Resource"),

    @Deprecated
    METHOD_FAILURE(420, Series.CLIENT_ERROR, "Method Failure"),

    @Deprecated
    DESTINATION_LOCKED(421, Series.CLIENT_ERROR, "Destination Locked"),

    UNPROCESSABLE_ENTITY(422, Series.CLIENT_ERROR, "Unprocessable Entity"),
    LOCKED(423, Series.CLIENT_ERROR, "Locked"),
    FAILURE_DEPENDENCY(424, Series.CLIENT_ERROR, "Failed Dependency"),
    TOO_EARLY(425, Series.CLIENT_ERROR, "Too Early"),
    UPGRADE_REQURIED(426, Series.CLIENT_ERROR, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, Series.CLIENT_ERROR, "Precondition Required"),
    TOO_MANY_REQUESTS(429, Series.CLIENT_ERROR, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, Series.CLIENT_ERROR, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, Series.CLIENT_ERROR, "Unavailable For Legal Reasons"),

    // 5xx Server Error
    INTERNAL_SERVER_ERROR(500, Series.SERVER_ERROR, "Internal Server Error"),
    NOT_IMPLEMENTED(501, Series.SERVER_ERROR, "Not Implemented"),
    BAD_GATEWAY(502, Series.SERVER_ERROR, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, Series.SERVER_ERROR, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, Series.SERVER_ERROR, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, Series.SERVER_ERROR, "HTTP Version not supported"),
    VARIANT_ALSO_NEGOTIATES(506, Series.SERVER_ERROR, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, Series.SERVER_ERROR, "Insufficient Storage"),
    LOOP_DETECTED(508, Series.SERVER_ERROR, "Loop Detected"),
    BANDWIDTH_LIMIT_EXCEEDED(509, Series.SERVER_ERROR, "Bandwidth Limit Exceeded"),
    NOT_EXTENDED(510, Series.SERVER_ERROR, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, Series.SERVER_ERROR, "Network Authentication Required");

    private static final CloneHttpStatus[] VALUES;

    static {
        VALUES = values();
    }

    private final int value;
    private final Series series;
    private final String reasonPhrase;

    CloneHttpStatus(int value, Series series, String reasonPhrase){
        this.value = value;
        this.series = series;
        this.reasonPhrase = reasonPhrase;
    }

    public int value(){
        return this.value;
    }

    public Series series(){
        return this.series;
    }

    public String getReasonPhrase(){
        return this.reasonPhrase;
    }

    public boolean is1xxInformational(){
        return (series() == Series.INFORMATIONAL);
    }

    public boolean is2xxSuccessful(){
        return (series() == Series.SUCCESSFUL);
    }

    public boolean is3xxRedirection(){
        return (series() == Series.REDIRECTION);
    }

    public boolean is4xxClientError(){
        return (series() == Series.CLIENT_ERROR);
    }

    public boolean is5xxServerError(){
        return (series() == Series.SERVER_ERROR);
    }

    public boolean isError(){
        return (is4xxClientError() || is5xxServerError());
    }

    @Override
    public String toString(){
        return this.value + " " + name();
    }

    public static CloneHttpStatus valueOf(int statusCode){
        CloneHttpStatus status = resolve(statusCode);
        if(status == null){
            throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
        }
        return status;
    }

    @Nullable
    public static CloneHttpStatus resolve(int statusCode){
        for(CloneHttpStatus status : VALUES) {
            if(status.value == statusCode){
                return status;
            }
        }
        return null;
    }

    public enum Series {
        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        private final int value;

        Series(int value){
            this.value = value;
        }

        public int value(){
            return this.value;
        }

        @Deprecated
        public static Series valueOf(CloneHttpStatus status){
            return status.series;
        }

        public static Series valueOf(int statusCode){
            Series series = resolve(statusCode);
            if(series == null){
                throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
            }
            return series;
        }

        @Nullable
        public static Series resolve(int statusCode){
            int seriesCode = statusCode / 100;
            for(Series series : values()){
                if(series.value == seriesCode){
                    return series;
                }
            }
            return null;
        }
    }

}
