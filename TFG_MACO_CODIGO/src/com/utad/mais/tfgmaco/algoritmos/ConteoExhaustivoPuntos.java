package com.utad.mais.tfgmaco.algoritmos;

import java.math.BigInteger;
import com.utad.mais.tfgmaco.curva.*;
import com.utad.mais.tfgmaco.operaciones.AritmeticaModular;

public class ConteoExhaustivoPuntos {
	
	public static BigInteger contarPuntos(CurvaElipticaFp curva) {
		
		BigInteger a = curva.getA();
		BigInteger b = curva.getB();
		BigInteger modulo = curva.getModulo();
			
		BigInteger numeroPuntos = BigInteger.ONE;   // Contamos el punto en el infinito
		
		
		// Establecer MOSTRAR_PUNTOS a true para imprimir por pantalla los primeros puntos de la curva encontrados
		boolean MOSTRAR_PUNTOS = false;
		int puntosImpresos = 0;
		final int MAX_IMPRESIONES = 50;
		if (MOSTRAR_PUNTOS) {
			System.out.println("\nPrimeros puntos de la curva (sin contar el punto en el infinito):");
		}
	
		
		// Por equivalencias modulares, solo probamos valores de y hasta (p-1)/2
		BigInteger limiteY = modulo.subtract(BigInteger.ONE).divide(BigInteger.TWO);
		
		// Recorremos todos los posibles valores de x desde 0 hasta modulo-1
		for (BigInteger x = BigInteger.ZERO; x.compareTo(modulo) < 0; x = x.add(BigInteger.ONE)) {
			
			// Lado derecho: x^3 + ax + b
			BigInteger xCubo = AritmeticaModular.exponenciar(x, BigInteger.valueOf(3), modulo);
			BigInteger ladoDerecho = AritmeticaModular.sumar(
					(BigInteger)(AritmeticaModular.sumar(xCubo, (BigInteger)(AritmeticaModular.multiplicar(a, x, modulo)), modulo)), 
					b, modulo);

			// Recorremos todos los posibles valores de y desde 0 hasta limiteY
			for (BigInteger y = BigInteger.ZERO; y.compareTo(limiteY) <= 0; y = y.add(BigInteger.ONE)) {
				
				// Lado izquierdo: y^2
				BigInteger ladoIzquiedo = AritmeticaModular.exponenciar(y, BigInteger.TWO, modulo);
				
				// Si se cumple la igualdad, el punto pertenece a la curva
				if (ladoIzquiedo.equals(ladoDerecho)) {
					
					// Por las equivalencias, y^2 ≡ (modulo - y)^2 menos para el valor y = 0
					if (y.equals(BigInteger.ZERO)) {
						numeroPuntos = numeroPuntos.add(BigInteger.ONE);
						
						// Si MOSTRAR_PUNTOS = true, imprimimos los primeros puntos que pertenecen a la curva
						if (MOSTRAR_PUNTOS && puntosImpresos < MAX_IMPRESIONES) {
							System.out.println("(" + x + ", " + y + ")");
							puntosImpresos++;
						}
						
					} else {
						numeroPuntos = numeroPuntos.add(BigInteger.TWO);
						
						if (MOSTRAR_PUNTOS && puntosImpresos < MAX_IMPRESIONES) {
				            System.out.println("(" + x + ", " + y + ")");
				            
				            BigInteger ySimetrica = modulo.subtract(y); 
				            System.out.println("(" + x + ", " + ySimetrica + ")");
				           
				            puntosImpresos += 2;
				        }
					}
					
					break;
				}
			}
		}
		
		return numeroPuntos;
		
	}
}