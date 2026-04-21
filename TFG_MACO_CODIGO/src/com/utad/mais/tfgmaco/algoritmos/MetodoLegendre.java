package com.utad.mais.tfgmaco.algoritmos;

import java.math.BigInteger;
import com.utad.mais.tfgmaco.curva.*;
import com.utad.mais.tfgmaco.operaciones.AritmeticaModular;

public class MetodoLegendre {
	public static BigInteger contarPuntos(CurvaElipticaFp curva) {
		
		BigInteger a = curva.getA();
		BigInteger b = curva.getB();
		BigInteger modulo = curva.getModulo();
		
		BigInteger numeroPuntos = BigInteger.ONE;   // Contamos el punto en el infinito
		BigInteger sumaSimboloLegendre = BigInteger.ZERO;
		
		// Con el modulo de la curva se calcula el exponente (p-1) / 2
		BigInteger exponente = modulo.subtract(BigInteger.ONE).divide(BigInteger.TWO);
		
		
		// Establecer MOSTRAR_PUNTOS a true para imprimir por pantalla los primeros puntos de la curva encontrados
		boolean MOSTRAR_PUNTOS = false;
		int puntosImpresos = 0;
		final int MAX_IMPRESIONES = 50;
		if (MOSTRAR_PUNTOS) {
			System.out.println("\nPrimeros puntos de la curva (sin contar el punto en el infinito):");
		}
		
		
		// Recorremos todos los posibles valores de x desde 0 hasta modulo-1
		for (BigInteger x = BigInteger.ZERO; x.compareTo(modulo) < 0; x = x.add(BigInteger.ONE)) {
			
			// Calculamos a_x = x^3 + a * x + b (mod p)
			BigInteger xCubo = AritmeticaModular.exponenciar(x, BigInteger.valueOf(3), modulo);
			BigInteger ax = AritmeticaModular.sumar(
					(BigInteger)(AritmeticaModular.sumar(xCubo, (BigInteger)(AritmeticaModular.multiplicar(a, x, modulo)), modulo)), 
					b, modulo);
			
			// Calculamos el simbolo de Legendre
			int simboloLegendre = calcularSimboloLegendre(ax, exponente, modulo);
			
			// Sumamos los simbolos de Legendre
			sumaSimboloLegendre = sumaSimboloLegendre.add(BigInteger.valueOf(simboloLegendre));
			
			// Si MOSTRAR_PUNTOS = true, imprimimos los primeros puntos que pertenecen a la curva
			if (MOSTRAR_PUNTOS && puntosImpresos < MAX_IMPRESIONES) {
				imprimirPuntos(x, ax, modulo);
				puntosImpresos++;
			}
		}
		
		// Calculamos el numero de puntos con la formula #E(F_p) = p + 1 + suma simbolos de Legendre
		numeroPuntos = modulo.add(BigInteger.ONE).add(sumaSimboloLegendre);
		
		return numeroPuntos;
	}
	
	public static int calcularSimboloLegendre(BigInteger ax, BigInteger exponente, BigInteger modulo) {
		int simboloLegendre = 0;
		
		// Segun el criterio de Euler, basta con calcular (a_x)^exponente
		BigInteger axExponencial = AritmeticaModular.exponenciar(ax, exponente, modulo);

		/* El criterio de Euler determina:
		 *      (a_x / p) = 1 si a ^ exponente ≡ 1 mod p
		 *      (a_x / p) = -1 si a ^ exponente ≡ -1 mod p ≡ (p - 1) mod p
		 */
		if (axExponencial.equals(BigInteger.ONE)) {
			simboloLegendre = 1;
		} else if (axExponencial.equals(modulo.subtract(BigInteger.ONE))) {
			simboloLegendre = -1;
		}
		
		return simboloLegendre;
	}
	
	public static void imprimirPuntos(BigInteger x, BigInteger ax, BigInteger modulo) {
		
		// Para imprimir puntos, recorremos todos los posibles valores de y desde 0 hasta modulo-1
		for (BigInteger y = BigInteger.ZERO; y.compareTo(modulo) < 0; y = y.add(BigInteger.ONE)) {
			
			// Lado izquierdo: y^2
			BigInteger ladoIzquiedo = AritmeticaModular.exponenciar(y, BigInteger.TWO, modulo);
			
			// Si se cumple la igualdad, el punto pertenece a la curva
			if (ladoIzquiedo.equals(ax)) {
				System.out.println("(" + x + ", " + y + ")");
			}
		}
	}
}
