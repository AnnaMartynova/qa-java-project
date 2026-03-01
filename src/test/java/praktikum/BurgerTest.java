package praktikum;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class BurgerTest {

    // ========== ТЕСТЫ КОНСТРУКТОРА ==========
    public static class ConstructorTest {
        private Burger burger;

        @Before
        public void setUp() {
            burger = new Burger();
        }

        @Test
        public void shouldCreateEmptyIngredientsList() {
            assertTrue(burger.ingredients.isEmpty());
        }

        @Test
        public void shouldSetBunToNull() {
            assertNull(burger.bun);
        }
    }

    // ========== ТЕСТЫ setBuns ==========
    public static class SetBunsTest {
        private Burger burger;
        @Mock private Bun mockBun;

        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);
            burger = new Burger();
        }

        @Test
        public void shouldAssignBun() {
            burger.setBuns(mockBun);
            assertEquals(mockBun, burger.bun);
        }
    }

    // ========== ТЕСТЫ addIngredient ==========
    public static class AddIngredientTest {
        private Burger burger;
        @Mock private Ingredient mockSauce;
        @Mock private Ingredient mockFilling;

        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);
            burger = new Burger();
        }

        @Test
        public void shouldIncreaseListSize() {
            burger.addIngredient(mockSauce);
            assertEquals(1, burger.ingredients.size());
        }

        @Test
        public void shouldAddIngredientToList() {
            burger.addIngredient(mockSauce);
            assertEquals(mockSauce, burger.ingredients.get(0));
        }

        @Test
        public void shouldMaintainOrder() {
            burger.addIngredient(mockSauce);
            burger.addIngredient(mockFilling);
            assertEquals(mockSauce, burger.ingredients.get(0));
            assertEquals(mockFilling, burger.ingredients.get(1));
        }
    }

    // ========== ТЕСТЫ removeIngredient ==========
    public static class RemoveIngredientTest {
        private Burger burger;
        @Mock private Ingredient mockSauce;
        @Mock private Ingredient mockFilling;

        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);
            burger = new Burger();
            burger.addIngredient(mockSauce);
            burger.addIngredient(mockFilling);
        }

        @Test
        public void shouldDecreaseListSize() {
            burger.removeIngredient(0);
            assertEquals(1, burger.ingredients.size());
        }

        @Test
        public void shouldRemoveCorrectElement() {
            burger.removeIngredient(0);
            assertEquals(mockFilling, burger.ingredients.get(0));
        }

        @Test(expected = IndexOutOfBoundsException.class)
        public void shouldThrowExceptionWhenIndexInvalid() {
            burger.removeIngredient(99);
        }
    }

    // ========== ТЕСТЫ moveIngredient ==========
    public static class MoveIngredientTest {
        private Burger burger;
        @Mock private Ingredient mockSauce;
        @Mock private Ingredient mockFilling;

        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);
            burger = new Burger();
            burger.addIngredient(mockSauce);
            burger.addIngredient(mockFilling);
        }

        @Test
        public void shouldMoveElement() {
            burger.moveIngredient(1, 0);
            assertEquals(mockFilling, burger.ingredients.get(0));
        }

        @Test
        public void shouldKeepOrderWhenMovingToSamePosition() {
            burger.moveIngredient(0, 0);
            assertEquals(mockSauce, burger.ingredients.get(0));
            assertEquals(mockFilling, burger.ingredients.get(1));
        }

        @Test(expected = IndexOutOfBoundsException.class)
        public void shouldThrowExceptionWhenIndexInvalid() {
            burger.moveIngredient(99, 0);
        }
    }

    // ========== ТЕСТЫ getPrice ==========
    public static class GetPriceTest {
        private Burger burger;
        @Mock private Bun mockBun;
        @Mock private Ingredient mockSauce;
        @Mock private Ingredient mockFilling;

        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);
            burger = new Burger();
        }

        @Test
        public void shouldReturnDoubleBunPriceWhenNoIngredients() {
            when(mockBun.getPrice()).thenReturn(150.0f);
            burger.setBuns(mockBun);
            assertEquals(300.0f, burger.getPrice(), 0.01);
        }

        @Test
        public void shouldAddIngredientPrices() {
            when(mockBun.getPrice()).thenReturn(100.0f);
            when(mockSauce.getPrice()).thenReturn(50.0f);
            when(mockFilling.getPrice()).thenReturn(30.0f);

            burger.setBuns(mockBun);
            burger.addIngredient(mockSauce);
            burger.addIngredient(mockFilling);

            assertEquals(280.0f, burger.getPrice(), 0.01);
        }

        @Test
        public void shouldCallBunGetPrice() {
            burger.setBuns(mockBun);
            burger.getPrice();
            verify(mockBun).getPrice();
        }
    }

    // ========== ПАРАМЕТРИЗОВАННЫЕ ТЕСТЫ getPrice ==========
    @RunWith(Parameterized.class)
    public static class GetPriceParameterizedTest {
        private final float bunPrice;
        private final List<Float> ingredientPrices;
        private final float expectedPrice;

        public GetPriceParameterizedTest(float bunPrice, List<Float> ingredientPrices, float expectedPrice) {
            this.bunPrice = bunPrice;
            this.ingredientPrices = ingredientPrices;
            this.expectedPrice = expectedPrice;
        }

        @Parameterized.Parameters(name = "{0} + {1} = {2}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {100.0f, Arrays.asList(50.0f, 30.0f), 280.0f},
                    {50.0f, Arrays.asList(10.0f, 20.0f, 30.0f), 160.0f},
                    {200.0f, Arrays.asList(), 400.0f},
                    {0.0f, Arrays.asList(10.0f, 20.0f), 30.0f},
            });
        }

        @Test
        public void shouldCalculateCorrectPrice() {
            Burger burger = new Burger();
            Bun mockBun = mock(Bun.class);
            when(mockBun.getPrice()).thenReturn(bunPrice);
            burger.setBuns(mockBun);

            for (Float price : ingredientPrices) {
                Ingredient mockIngredient = mock(Ingredient.class);
                when(mockIngredient.getPrice()).thenReturn(price);
                burger.addIngredient(mockIngredient);
            }

            assertEquals(expectedPrice, burger.getPrice(), 0.01);
        }
    }

    // ========== ТЕСТЫ getReceipt ==========
    public static class GetReceiptTest {
        private Burger burger;
        @Mock private Bun mockBun;
        @Mock private Ingredient mockSauce;
        @Mock private Ingredient mockFilling;

        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);
            burger = new Burger();
        }

        private void setupBun(String name) {
            when(mockBun.getName()).thenReturn(name);
            burger.setBuns(mockBun);
        }

        @Test
        public void shouldContainBunName() {
            setupBun("black bun");
            String receipt = burger.getReceipt();
            assertTrue(receipt.contains("black bun"));
        }

        @Test
        public void shouldContainSauceLine() {
            setupBun("bun");
            when(mockSauce.getType()).thenReturn(IngredientType.SAUCE);
            when(mockSauce.getName()).thenReturn("hot sauce");
            burger.addIngredient(mockSauce);

            String receipt = burger.getReceipt();
            assertTrue(receipt.contains("= sauce hot sauce ="));
        }

        @Test
        public void shouldContainFillingLine() {
            setupBun("bun");
            when(mockFilling.getType()).thenReturn(IngredientType.FILLING);
            when(mockFilling.getName()).thenReturn("cutlet");
            burger.addIngredient(mockFilling);

            String receipt = burger.getReceipt();
            assertTrue(receipt.contains("= filling cutlet ="));
        }

        @Test
        public void shouldContainPrice() {
            setupBun("bun");
            when(mockBun.getPrice()).thenReturn(100.0f);

            String receipt = burger.getReceipt();
            assertTrue(receipt.contains("Price: 200,000000"));
        }

        @Test
        public void shouldHaveFourLinesWhenNoIngredients() {
            setupBun("bun");
            String[] lines = burger.getReceipt().split("\\n");
            assertEquals(4, lines.length);
        }
    }

    // ========== ИНТЕГРАЦИОННЫЕ ТЕСТЫ ==========
    public static class IntegrationTest {

        @Test
        public void shouldWorkWithRealObjects() {
            Burger burger = new Burger();
            Bun bun = new Bun("test bun", 100);
            Ingredient sauce = new Ingredient(IngredientType.SAUCE, "test sauce", 50);

            burger.setBuns(bun);
            burger.addIngredient(sauce);

            assertEquals(250.0f, burger.getPrice(), 0.01);
            assertTrue(burger.getReceipt().contains("test bun"));
            assertTrue(burger.getReceipt().contains("sauce test sauce"));
        }
    }
}