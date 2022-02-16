import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Consumer;


/**
 * 왜 사용할까?
 * 사용 편의성 때문이라고 생각한다.
 * Rest 컨트롤러의 응답으로 json을 반환할 경우 Map을 만들고 Gson.parse()를 이용하였다.
 * 하지만 Map의 경우 String key값과 Object Value를 사용하여 매 메서드마다 다르게
 * 구조를 만들어 내리는 경우도 있었다.
 *
 * ResponseEntity를 사용하면 ok일 경우 응답 json 안에는 Type에 해당하는 객체만 표시되며,
 * 에러일 경우 상태코드, 메시지 등의 포맷이 갖춰져 있어 커스텀할 필요가 없어 쓰기 간편하다고 생각한다.
 *
 * 제네릭은 왜 사용할까?
 * 클래스와 메서드 등을 여러 타입에 적용하여 사용하고 싶을 때 쓰이는 키워드 같다.
 * 하지만 정확한 이유는 아래와 같다.
 *
 * 자바에서 제네릭(generic)이란 데이터의 타입(data type)을 일반화한다(generalize)는 것을 의미합니다.
 * 제네릭은 클래스나 메소드에서 사용할 내부 데이터 타입을 컴파일 시에 미리 지정하는 방법입니다.
 * 이렇게 컴파일 시에 미리 타입 검사(type check)를 수행하면 다음과 같은 장점을 가집니다.
 *
 * 결국 런타임 시에 발생할 수 있는 형 안정성 문제를 컴파일 단계에서 해결한다는 것이다.
 * 한가지 예시로는 Object 클래스로 데이터를 주고 받을 때 형변환 시 발생할 수 있는 에러가 있다.
 */
public class CloneResponseEntity<T> extends HttpEntity<T> {

    private final Object status;

    public CloneResponseEntity(HttpStatus status){
        this(null, null, status);
    }

    public CloneResponseEntity(@Nullable T body, HttpStatus status){
        this(body, null, status);
    }

    public CloneResponseEntity(MultiValueMap<String, String> headers, HttpStatus status){
        this(null, headers, status);
    }

    public CloneResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, HttpStatus status){
        this(body, headers, (Object) status);
    }

    public CloneResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, int rawStatus){
        this(body, headers, (Object) rawStatus);
    }

    // @Nullable은 파라미터가 null일수도 있을 때 사용한다.
    private CloneResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, Object status){
        super(body, headers);
        Assert.notNull(status, "HttpStatus must not be null");
        this.status = status;
    }

    public HttpStatus getStatusCode(){
        if(this.status instanceof HttpStatus){
            return (HttpStatus) this.status;
        }else{
            return HttpStatus.valueOf((Integer) this.status);
        }
    }

    public int getStatusCodeValue(){
        if(this.status instanceof HttpStatus){
            return ((HttpStatus) this.status).value();
        }else{
            return (Integer) this.status;
        }
    }

    @Override
    public boolean equals(@Nullable Object other){
        if(this == other){
            return true;
        }
        if(!super.equals(other)){
            return false;
        }
        CloneResponseEntity<?> otherEntity = (CloneResponseEntity<?>) other;
        return ObjectUtils.nullSafeEquals(this.status, otherEntity.status);
    }

    @Override
    public int hashCode(){
        return (29 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.status));
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder("<");
        builder.append(this.status);
        if(this.status instanceof HttpStatus){
            builder.append(' ');
            builder.append(((HttpStatus) this.status).getReasonPhrase());
        }
        builder.append(',');
        T body = getBody();
        HttpHeaders headers = getHeaders();
        if(body != null){
            builder.append(body);
            builder.append(',');
        }
        builder.append(headers);
        builder.append('>');
        return builder.toString();
    }

    public static BodyBuilder status(HttpStatus status){
        Assert.notNull(status, "HttpStatus must not be null");
        return new DefaultBuilder(status);
    }

    public static BodyBuilder status(int status){
        return new DefaultBuilder(status);
    }

    public static BodyBuilder ok(){
        return status(HttpStatus.OK);
    }

    public static <T> CloneResponseEntity<T> ok(@Nullable T body){
        return ok().body(body);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> CloneResponseEntity<T> of(Optional<T> body){
        Assert.notNull(body, "Body must not be null");
        return body.map(CloneResponseEntity::ok).orElseGet(() -> notFound().build());
    }

    public static BodyBuilder created(URI location){
        return status(HttpStatus.CREATED).location(location);
    }

    public static BodyBuilder accepted() {
        return status(HttpStatus.ACCEPTED);
    }

    public static HeadersBuilder<?> noContent() {
        return status(HttpStatus.NO_CONTENT);
    }

    public static BodyBuilder badRequest(){
        return status(HttpStatus.BAD_REQUEST);
    }

    public static HeadersBuilder<?> notFound(){
        return status(HttpStatus.NOT_FOUND);
    }

    public static BodyBuilder unprocessableEntity(){
        return status(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public static BodyBuilder internalServerError(){
        return status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public interface HeadersBuilder<B extends HeadersBuilder<B>> {
        B header(String headerName, String... headerValues);

        B headers(@Nullable HttpHeaders headers);

        B headers(Consumer<HttpHeaders> headersConsumer);

        B allow(HttpMethod... allowedMethods);

        B eTag(String eTag);

        B lastModified(ZonedDateTime lastModified);

        B lastModified(Instant lastModified);

        B lastModified(long lastModified);

        B location(URI location);

        B cacheControl(CacheControl cacheControl);

        B varyBy(String... requestHeaders);

        <T> CloneResponseEntity<T> build();
    }

    public interface BodyBuilder extends HeadersBuilder<BodyBuilder>{
        BodyBuilder contentLength(long contentLength);
        BodyBuilder contentType(MediaType contentType);
        <T> CloneResponseEntity<T> body(@Nullable T body);
    }

    private static class DefaultBuilder implements BodyBuilder {
        private final Object statusCode;
        private final HttpHeaders headers = new HttpHeaders();
        public DefaultBuilder(Object statusCode){
            this.statusCode = statusCode;
        }

        @Override
        public BodyBuilder header(String headerName, String... headerValues){
            for(String headerValue : headerValues){
                this.headers.add(headerName, headerValue);
            }
            return this;
        }

        @Override
        public BodyBuilder headers(@Nullable HttpHeaders headers){
            if(headers != null){
                this.headers.putAll(headers);
            }
            return this;
        }

        @Override
        public BodyBuilder headers(Consumer<HttpHeaders> headersConsumer){
            headersConsumer.accept(this.headers);
            return this;
        }

        @Override
        public BodyBuilder allow(HttpMethod... allowedMethods){
            this.headers.setAllow(new LinkedHashSet<>(Arrays.asList(allowedMethods)));
            return this;
        }

        @Override
        public BodyBuilder contentLength(long contentLength){
            this.headers.setContentLength(contentLength);
            return this;
        }

        @Override
        public BodyBuilder contentType(MediaType contentType){
            this.headers.setContentType(contentType);
            return this;
        }

        @Override
        public BodyBuilder eTag(String etag){
            if(!etag.startsWith("\"") && !etag.startsWith("W/\"")){
                etag = "\"" + etag;
            }
            if(!etag.endsWith("\"")){
                etag = etag + "\"";
            }
            this.headers.setETag(etag);
            return this;
        }

        @Override
        public BodyBuilder lastModified(ZonedDateTime date){
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public BodyBuilder lastModified(Instant date){
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public BodyBuilder lastModified(long date){
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public BodyBuilder location(URI location){
            this.headers.setLocation(location);
            return this;
        }

        @Override
        public BodyBuilder cacheControl(CacheControl cacheControl){
            this.headers.setCacheControl(cacheControl);
            return this;
        }

        @Override
        public BodyBuilder varyBy(String... requestHeaders){
            this.headers.setVary(Arrays.asList(requestHeaders));
            return this;
        }

        @Override
        public <T> CloneResponseEntity<T> build(){
            return body(null);
        }

        @Override
        public <T> CloneResponseEntity<T> body(@Nullable T body){
            return new CloneResponseEntity<>(body, this.headers, this.statusCode);
        }
    }

}
