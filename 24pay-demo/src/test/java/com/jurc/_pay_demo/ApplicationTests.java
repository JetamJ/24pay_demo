package com.jurc._pay_demo;

import com.jurc._pay_demo.config.PaymentProperties;
import com.jurc._pay_demo.service.SignService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private SignService signService;
	@Autowired
	private PaymentProperties properties;

	@Test
	void testGenerateSign_shouldReturn32CharHex() {
		String mid = properties.getMid();
		String amount = "1.00";
		String currency = properties.getCurrAlphaCode();
		String msTxnId = properties.getMsTxnId();
		String firstName = properties.getFirstName();
		String familyName = properties.getFamilyName();
		String timestamp = "2014-12-01 13:00:00";
		String key = properties.getKey();

		String message = mid + amount + currency + msTxnId + firstName + familyName + timestamp;
		String sign = signService.generateSign(message, key, properties.getMid());

		System.out.println(sign);
		assertNotNull(sign);
		assertEquals(32, sign.length());
		assertTrue(sign.matches("[0-9a-fA-F]+"));
	}

	@Test
	void testVerifySignature_knownValues_shouldReturnTrue() {
		String msTxnId = "1234567890";
		String amount = "1.00";
		String currCode = "EUR";
		String result = "OK";
		String sign = "21f22ef2af21d3819cd0cff06ef55943";
		String key = properties.getKey();

		String message = msTxnId + amount + currCode + result;
		String checkSign = signService.generateSign(message, key, properties.getMid());

		assertEquals(sign, checkSign);
	}

	@Test
	void testVerifySignature_invalidSign_shouldReturnFalse() {
		String msTxnId = "1234567890";
		String amount = "1.00";
		String currCode = "EUR";
		String result = "OK";
		String sign = "00000000000000000000000000000000";
		String key = properties.getKey();

		String message = msTxnId + amount + currCode + result;
		String checkSign = signService.generateSign(message, key, properties.getMid());

		assertNotEquals(sign, checkSign);
	}
}
