import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloneResponseEntityTest {
    /**
     * 써보니 어떤가?
     * 컴파일 시 내부 데이터 타입이 정해지다보니 여러 클래스의 객체를 담을 수 있다.
     * 제네릭은 형안정성의 가장 큰 이유이다.
     * 또한 사용의 측면에서는 택배의 실어놓는 택배 박스 같다.
     * 깨지기 쉬운 택배 박스, 그냥 놔둬되는 박스 등등...
     *
     * 트럭은 어떠한 타입의 박스를 취급하는지에 따라 실고 내리는 것을 달리 해야 한다.
     */
    @Test
    public void test(){
        String result = "123";
        CloneResponseEntity<String> entity = CloneResponseEntity.ok(result);

        System.out.println(entity);
    }

}