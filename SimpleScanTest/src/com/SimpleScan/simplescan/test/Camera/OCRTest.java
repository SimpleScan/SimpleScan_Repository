package com.SimpleScan.simplescan.test.Camera;

import com.SimpleScan.simplescan.Camera.OCR;

import android.test.AndroidTestCase;

public class OCRTest extends AndroidTestCase{
	
	public void testAmtStr2Double(){
		String amt_4decimals = "123.4321";
		assertEquals(123.4321, OCR.amtStr2double(amt_4decimals));
		String amt_2decimals = "2123.12";
		assertEquals(2123.12, OCR.amtStr2double(amt_2decimals));
		String amt_1decimal = "1.2";
		assertEquals(1.2, OCR.amtStr2double(amt_1decimal));
		String amt_0decimal = "23";
		assertEquals(23.0, OCR.amtStr2double(amt_0decimal));
		String amt_zero = "0";
		assertEquals(0.0, OCR.amtStr2double(amt_zero));
		String amt_dollarSign = "$32.12";
		assertEquals(32.12, OCR.amtStr2double(amt_dollarSign));
	}
}
