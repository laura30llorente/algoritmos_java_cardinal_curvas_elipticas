package com.utad.mais.tfgmaco.main;

import java.math.BigInteger;
import java.util.Scanner;
import com.utad.mais.tfgmaco.algoritmos.*;
import com.utad.mais.tfgmaco.curva.*;
import com.utad.mais.tfgmaco.utilidades.*;

public class ConteoPuntosMain {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// Creamos una curva
		System.out.print("Valor del coeficiente a de la curva: ");
		BigInteger a = scanner.nextBigInteger();
		System.out.print("Valor del coeficiente b de la curva: ");
		BigInteger b = scanner.nextBigInteger();
		System.out.print("Valor del modulo de la curva: ");
		BigInteger modulo = scanner.nextBigInteger();
		
		if (!modulo.isProbablePrime(100)) {
			throw new IllegalArgumentException("El modulo debe ser un numero primo");
		}

		CurvaElipticaFp curva = new CurvaElipticaFp(a, b, modulo);
	
		// Mostramos los detalles de la curva
		ImpresionDetalles.imprimirInformacionCurva(curva);
		
		// Elegimos el metodo de conteo de puntos
		System.out.print("\nElige un metodo de conteo de puntos: ");
		System.out.print("\n   1 - Conteo exhaustivo de todos los pares de elementos");
		System.out.print("\n   2 - Metodo con el simbolo de Legendre");
		System.out.print("\n   3 - Escaneo del rango");
		System.out.print("\n   4 - Algoritmo Schoof");
		System.out.print("\nEleccion: ");
		int metodo = scanner.nextInt();
		
		String nombreMetodo = "";
		BigInteger numeroPuntos = BigInteger.ZERO;
		long inicio = 0;
		long fin = 0;
		
		switch (metodo) {
			// METODO 1: Conteo exhaustivo de todos los pares de elementos
			case 1:
				nombreMetodo = "Conteo exhaustivo de todos los pares de elementos";
				inicio = System.currentTimeMillis();
				numeroPuntos = ConteoExhaustivoPuntos.contarPuntos(curva);
				fin = System.currentTimeMillis();
				
				break;
				
			// METODO 2: Metodo con el simbolo de Legendre
			case 2:
				nombreMetodo = "Metodo con el simbolo de Legendre";
				inicio = System.currentTimeMillis();
				numeroPuntos = MetodoLegendre.contarPuntos(curva);
				fin = System.currentTimeMillis();
				
				break;
			
			// METODO 3: Escaneo del rango
			case 3:
				nombreMetodo = "Escaneo del rango";
				inicio = System.currentTimeMillis();
				numeroPuntos = EscaneoRango.contarPuntos(curva);
				fin = System.currentTimeMillis();
				
				break;
			
			// METODO 4: Algoritmo de Schoof
			case 4:
				nombreMetodo = "Algoritmo de Schoof";
				inicio = System.currentTimeMillis();
				numeroPuntos = AlgoritmoSchoof.contarPuntos(curva);
				fin = System.currentTimeMillis();
				
				break;

			default:
				System.out.print("Numero de metodo no valido");
		}
		
		// Mostramos los detalles del conteo realizado segun el metodo elegido
		ImpresionDetalles.imprimirInformacionConteo(nombreMetodo, numeroPuntos, fin - inicio);
	}

}
