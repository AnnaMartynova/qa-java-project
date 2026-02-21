package praktikum;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BurgerTest {

    private Burger burger;

    @Mock
    private Bun mockBun;

    @Mock
    private Ingredient mockIngredient1;

    @Mock
    private Ingredient mockIngredient2;

    @Mock
    private Ingredient mockIngredient3;

    @Before
    public void setUp() {
        burger = new Burger();
    }

    @Test
    public void testSetBuns() {
        burger.setBuns(mockBun);
        assertEquals(mockBun, burger.bun);
    }

    @Test
    public void testAddIngredient() {
        burger.addIngredient(mockIngredient1);
        assertEquals(1, burger.ingredients.size());
        assertEquals(mockIngredient1, burger.ingredients.get(0));
    }

    @Test
    public void testRemoveIngredient() {
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);

        burger.removeIngredient(0);

        assertEquals(1, burger.ingredients.size());
        assertEquals(mockIngredient2, burger.ingredients.get(0));
    }

    @Test
    public void testMoveIngredient() {
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);
        burger.addIngredient(mockIngredient3);

        burger.moveIngredient(2, 0);

        assertEquals(mockIngredient3, burger.ingredients.get(0));
        assertEquals(mockIngredient1, burger.ingredients.get(1));
        assertEquals(mockIngredient2, burger.ingredients.get(2));
    }

    @Test
    public void testGetPrice() {
        when(mockBun.getPrice()).thenReturn(100f);
        when(mockIngredient1.getPrice()).thenReturn(50f);
        when(mockIngredient2.getPrice()).thenReturn(30f);

        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);

        float expectedPrice = 100f * 2 + 50f + 30f; // 280
        float actualPrice = burger.getPrice();

        assertEquals(expectedPrice, actualPrice, 0.01f);
    }

    @Test
    public void testGetReceipt() {
        when(mockBun.getName()).thenReturn("black bun");
        when(mockIngredient1.getType()).thenReturn(IngredientType.SAUCE);
        when(mockIngredient1.getName()).thenReturn("hot sauce");
        when(mockIngredient2.getType()).thenReturn(IngredientType.FILLING);
        when(mockIngredient2.getName()).thenReturn("cutlet");
        when(mockBun.getPrice()).thenReturn(100f);
        when(mockIngredient1.getPrice()).thenReturn(50f);
        when(mockIngredient2.getPrice()).thenReturn(80f);

        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);

        String expectedReceipt = String.format("(==== black bun ====)%n") +
                String.format("= sauce hot sauce =%n") +
                String.format("= filling cutlet =%n") +
                String.format("(==== black bun ====)%n") +
                String.format("%nPrice: 330,000000%n");

        String actualReceipt = burger.getReceipt();

        assertEquals(expectedReceipt, actualReceipt);

        verify(mockBun, atLeast(2)).getName();
        verify(mockIngredient1).getType();
        verify(mockIngredient1).getName();
        verify(mockIngredient2).getType();
        verify(mockIngredient2).getName();
    }

    @Test
    public void testGetReceiptWithNoIngredients() {
        when(mockBun.getName()).thenReturn("white bun");
        when(mockBun.getPrice()).thenReturn(150f);

        burger.setBuns(mockBun);

        String expectedReceipt = String.format("(==== white bun ====)%n") +
                String.format("(==== white bun ====)%n") +
                String.format("%nPrice: 300,000000%n");

        String actualReceipt = burger.getReceipt();

        assertEquals(expectedReceipt, actualReceipt);
    }

    @Test
    public void testAddMultipleIngredients() {
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);
        burger.addIngredient(mockIngredient3);

        assertEquals(3, burger.ingredients.size());
        assertEquals(mockIngredient1, burger.ingredients.get(0));
        assertEquals(mockIngredient2, burger.ingredients.get(1));
        assertEquals(mockIngredient3, burger.ingredients.get(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveNonExistentIngredient() {
        burger.removeIngredient(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIngredientWithInvalidIndex() {
        burger.addIngredient(mockIngredient1);
        burger.moveIngredient(2, 0);
    }

    @Test
    public void testWithDatabaseStub() {
        // Создаем стаб для Database
        Database databaseStub = mock(Database.class);

        // Создаем реальные объекты для стаба
        Bun realBun = new Bun("test bun", 150f);
        Ingredient realIngredient = new Ingredient(IngredientType.SAUCE, "test sauce", 75f);

        // Настраиваем стаб
        when(databaseStub.availableBuns()).thenReturn(Arrays.asList(realBun));
        when(databaseStub.availableIngredients()).thenReturn(Arrays.asList(realIngredient));

        // Используем стаб
        List<Bun> buns = databaseStub.availableBuns();
        List<Ingredient> ingredients = databaseStub.availableIngredients();

        burger.setBuns(buns.get(0));
        burger.addIngredient(ingredients.get(0));

        assertEquals(realBun, burger.bun);
        assertEquals(realIngredient, burger.ingredients.get(0));
        assertEquals(375f, burger.getPrice(), 0.01f); // 150*2 + 75 = 375
    }

    // Параметризованный тест ВНУТРИ того же класса
    @RunWith(Parameterized.class)
    public static class BurgerPriceParameterizedTest {

        private float bunPrice;
        private List<Float> ingredientPrices;
        private float expectedPrice;

        public BurgerPriceParameterizedTest(float bunPrice, List<Float> ingredientPrices, float expectedPrice) {
            this.bunPrice = bunPrice;
            this.ingredientPrices = ingredientPrices;
            this.expectedPrice = expectedPrice;
        }

        @Parameterized.Parameters
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {100f, Arrays.asList(50f, 30f), 280f},
                    {50f, Arrays.asList(25f, 25f, 25f), 175f},
                    {200f, Arrays.asList(100f), 500f},
                    {0f, Arrays.asList(100f, 50f), 150f},
                    {150f, Arrays.asList(), 300f}
            });
        }

        @Test
        public void testGetPriceWithParameters() {
            // Создаем моки
            Bun bun = mock(Bun.class);
            when(bun.getPrice()).thenReturn(bunPrice);

            Burger testBurger = new Burger();
            testBurger.setBuns(bun);

            // Добавляем ингредиенты с заданными ценами
            for (Float price : ingredientPrices) {
                Ingredient ingredient = mock(Ingredient.class);
                when(ingredient.getPrice()).thenReturn(price);
                testBurger.addIngredient(ingredient);
            }

            float actualPrice = testBurger.getPrice();
            assertEquals(expectedPrice, actualPrice, 0.01f);
        }
    }
}