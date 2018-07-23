package seng302.model;

import org.junit.Before;
import org.junit.Test;
import seng302.model.UserAttributeCollection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


/**
 * A class for unit testing the methods of the UserAttributeCollection class.
 */
public class UserAttributeCollectionTest {


    private UserAttributeCollection userAppCol;

    @Before
    public void setUp(){

        userAppCol = new UserAttributeCollection(2.0, 51.0, "AB-", "1 Elm Street",
                "", "cashmere", "christchurch", 5678, "NZ", false, "130/80", false, 2.0, "");

    }

    /**
     * testing boundry cases for valid heights.
     */
    @Test
    public void validateHeightTest1(){
        assertTrue(userAppCol.validateHeight(1.0));
    }

    /**
     * testing boundry cases for invalid heights.
     */
    @Test
    public void validateHeightTest2(){
        assertFalse(userAppCol.validateHeight(-0.01));
    }

    /**
     * testing boundry cases for valid heights.
     */
    @Test
    public void validateHeightTest3(){
        assertTrue(userAppCol.validateHeight(3.99));
    }

    /**
     * testing boundry cases for invalid heights.
     */
    @Test
    public void validateHeightTest4(){
        assertFalse(userAppCol.validateHeight(4.0001));
    }


    /**
     * testing boundry cases for valid weights.
     */
    @Test
    public void validateWeightTest1(){
        assertTrue(userAppCol.validateWeight(50.0));
    }

    /**
     * testing boundry cases for invalid weights.
     */
    @Test
    public void validateWeightTest2(){
        assertFalse(userAppCol.validateWeight(-0.01));
    }

    /**
     * testing success cases for validateBloodType.
     */
    @Test
    public void validateBloodTypeTest1(){
        assertTrue(userAppCol.validateBloodType("A-"));
    }

    /**
     * testing success cases for validateBloodType.
     */
    @Test
    public void validateBloodTypeTest2(){
        assertTrue(userAppCol.validateBloodType("AB+"));
    }

    /**
     * testing failure cases for validateBloodType when string is in lower case.
     */
    @Test
    public void validateBloodTypeTest3(){
        assertFalse(userAppCol.validateBloodType("o+"));
    }

    /**
     * testing failure cases for validateBloodType - wrong letter.
     */
    @Test
    public void validateBloodTypeTest4(){
        assertFalse(userAppCol.validateBloodType("F-"));
    }

    /**
     * testing failure cases for validateBloodType - mis-ordered letters.
     */
    @Test
    public void validateBloodTypeTest5(){
        assertFalse(userAppCol.validateBloodType("BA+"));
    }

    /**
     * testing failure cases for validateBloodType - bad string.
     */
    @Test
    public void validateBloodTypeTest6(){
        assertFalse(userAppCol.validateBloodType("123+"));
    }

    /**
     * testing failure cases for validateBloodType - duplicated letters.
     */
    @Test
    public void validateBloodTypeTest7(){
        assertFalse(userAppCol.validateBloodType("OO+"));
    }

    /**
     * testing failure cases for validateBloodType - empty string.
     */
    @Test
    public void validateBloodTypeTest8(){
        assertFalse(userAppCol.validateBloodType(""));
    }


    /**
     * testing failure cases for validateBloodType - duplicated symbols.
     */
    @Test
    public void validateBloodTypeTest9(){
        assertFalse(userAppCol.validateBloodType("A++"));
    }

    /**
     * testing failure cases for validateBloodType - mis-ordered symbols.
     */
    @Test
    public void validateBloodTypeTest10(){
        assertFalse(userAppCol.validateBloodType("-B"));
    }

    /**
     * testing success cases for validateAlphanumericString - Address 1.
     */
    @Test
    public void validateAlphanumericStringTest1(){
        assertTrue(userAppCol.validateAlphanumericString(true, "666, Elm-Street'", 0, 100));
    }

    /**
     * testing boundry success cases for validateAlphanumericString - min string length.
     */
    @Test
    public void validateAlphanumericStringTest2(){
        assertTrue(userAppCol.validateAlphanumericString(true, "1", 0, 100));
    }

    /**
     * testing boundry success cases for validateAlphanumericString - max string length.
     */
    @Test
    public void validateAlphanumericStringTest3(){
        assertTrue(userAppCol.validateAlphanumericString(true, "fishyfishyfishyfishyfishyfishyfishy" +
                "fishyfishyfishyfishyfishyfishyfishyfishyfishyfishyfishyfishyfishy", 0, 100));
    }

    /**
     * testing success cases for validateAlphanumericString - alphabetic string.
     */
    @Test
    public void validateAlphanumericStringTest4(){
        assertTrue(userAppCol.validateAlphanumericString(false, "ten Moe's place", 0, 100));
    }

    /**
     * testing success cases for validateAlphanumericString - alphabetic country code of length = 2.
     */
    @Test
    public void validateAlphanumericStringTest5(){
        assertTrue(userAppCol.validateAlphanumericString(false, "nZ", 2, 2));
    }


    /**
     * testing failure cases for validateAlphanumericString - non alphabetic string.
     */
    @Test
    public void validateAlphanumericStringTest6(){
        assertFalse(userAppCol.validateAlphanumericString(false, "abc123", 0, 100));
    }


    /**
     * testing failure cases for validateAlphanumericString - minLength is greater than maxLength.
     */
    @Test
    public void validateAlphanumericStringTest7(){
        assertFalse(userAppCol.validateAlphanumericString(false, "abc123", 9, 8));
    }

    /**
     * testing failure cases for validateAlphanumericString - invalid characters.
     */
    @Test
    public void validateAlphanumericStringTest8(){
        assertFalse(userAppCol.validateAlphanumericString(true, "abc123*&^", 0, 100));
    }

    /**
     * testing failure cases for validateAlphanumericString - invalid length (empty string).
     */
    @Test
    public void validateAlphanumericStringTest9(){
        assertFalse(userAppCol.validateAlphanumericString(true, "", 1, 100));
    }

    /**
     * testing success cases for validateAlphanumericString - valid length (empty string).
     */
    @Test
    public void validateAlphanumericStringTest10(){
        assertTrue(userAppCol.validateAlphanumericString(true, "", 0, 100));
    }

    /**
     * testing failure cases for validateAlphanumericString - invalid boundry length (too long).
     */
    @Test
    public void validateAlphanumericStringTest11(){
        assertFalse(userAppCol.validateAlphanumericString(true, "fishyfishy1", 1, 10));
    }

    /**
     * tests success case for validatePostCode - A normal post code.
     */
    @Test
    public void validatePostCodeTest1(){
        assertTrue(userAppCol.validatePostCode(3842));
    }


    /**
     * tests success case for validatePostCode - A  boundry number with leading zeros.
     */
    @Test
    public void validatePostCodeTest2(){
        assertTrue(userAppCol.validatePostCode(0001));
    }

    /**
     * tests success case for validatePostCode - Upper boundry post code.
     */
    @Test
    public void validatePostCodeTest3(){
        assertTrue(userAppCol.validatePostCode(9999));
    }

    /**
     * tests success case for validatePostCode - Post Code with 3 digits.
     */
    @Test
    public void validatePostCodeTest6(){
        assertTrue(userAppCol.validatePostCode(456));
    }

    /**
     * tests failure case for validatePostCode - Lower boundry post code.
     */
    @Test
    public void validatePostCodeTest4(){
        assertFalse(userAppCol.validatePostCode(0000));
    }

    /**
     * tests failure case for validatePostCode - Upper boundry post code and code exceeding 4 digits.
     */
    @Test
    public void validatePostCodeTest5(){
        assertFalse(userAppCol.validatePostCode(10000));
    }

    /**
     * test for success case when all the User attributes print out as expected.
     */
    @Test
    public void printAllAttributesTest(){
        String string = "User Attributes:\n\tHeight: 2.0m\n\tWeight: 51.0kg\n\tBlood Type: AB-\n\tBlood Pressure: 130/80\n\tSmoker: false\n\tAlcohol Consumption: 2.00 standard drinks per week\n\nAddress:\n\t1 Elm Street" +
                "\n\t\n\tcashmere\n\tchristchurch\n\t5678\n\tNZ\n\n";
        System.out.println(string);
        assertEquals(userAppCol.toString(), string);
    }





}


