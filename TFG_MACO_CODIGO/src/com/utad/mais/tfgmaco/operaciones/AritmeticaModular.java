package com.utad.mais.tfgmaco.operaciones;

import java.math.BigInteger;

public class AritmeticaModular {
	
	// SUMA MODULAR: (a + b) mod modulo
	public static BigInteger sumar(BigInteger a, BigInteger b, BigInteger modulo) {
		return a.add(b).mod(modulo);
	}
	
	// RESTA MODULAR: (a - b) mod modulo
	public static BigInteger restar(BigInteger a, BigInteger b, BigInteger modulo) {
		return a.subtract(b).mod(modulo);
	}
	
	// MULTIPLICACION MODULAR: (a * b) mod modulo
	public static BigInteger multiplicar(BigInteger a, BigInteger b, BigInteger modulo) {
		return a.multiply(b).mod(modulo);
	}
	
	// INVERSO MODULAR: a^(-1) mod modulo
	public static BigInteger inverso(BigInteger a, BigInteger modulo) {
		return a.modInverse(modulo);
	}
	
	// DIVISON MODULAR: (a / b) mod modulo = (a * b^(-1)) mod modulo
	public static BigInteger division(BigInteger a, BigInteger b, BigInteger modulo) {
		if (b.mod(modulo).equals(BigInteger.ZERO)) {
			throw new ArithmeticException("ERROR: Division por cero");
		}
		return multiplicar(a, inverso(b, modulo), modulo);
	}
	
	// EXPONENCIACION MODULAR: base^exponente mod modulo
	public static BigInteger exponenciar(BigInteger base, BigInteger exponente, BigInteger modulo) {
		return base.modPow(exponente, modulo);
	}
}