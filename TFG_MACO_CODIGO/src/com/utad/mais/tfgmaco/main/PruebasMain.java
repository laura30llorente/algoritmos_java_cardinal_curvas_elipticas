package com.utad.mais.tfgmaco.main;

import java.math.BigInteger;
import java.util.Scanner;

import com.utad.mais.tfgmaco.curva.CurvaElipticaFp;
import com.utad.mais.tfgmaco.pruebas.PruebaConteoExhaustivo;
import com.utad.mais.tfgmaco.pruebas.PruebaEscaneoRango;
import com.utad.mais.tfgmaco.pruebas.PruebaSimboloLegendre;
import com.utad.mais.tfgmaco.utilidades.ImpresionDetalles;

public class PruebasMain {

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
		
		// Elegimos el metodo de conteo de puntos para realizar la estimacion temporal
		System.out.print("\nElige un metodo de conteo de puntos para realizar la estimacion temporal: ");
		System.out.print("\n   1 - Conteo exhaustivo de todos los pares de elementos");
		System.out.print("\n   2 - Metodo con el simbolo de Legendre");
		System.out.print("\n   3 - Escaneo del rango");
		System.out.print("\nEleccion: ");
		int metodo = scanner.nextInt();
				
		switch (metodo) {
			// METODO 1: Conteo exhaustivo de todos los pares de elementos
			case 1:
				PruebaConteoExhaustivo.MetricasConteoExhaustivo m1 = PruebaConteoExhaustivo.estimarTiempo(curva);
				PruebaConteoExhaustivo.imprimirTiempoEstimado(m1);
		
				break;
				
			// METODO 2: Metodo con el simbolo de Legendre
			case 2:
				PruebaSimboloLegendre.MetricasSimboloLegendre m2 = PruebaSimboloLegendre.estimarTiempo(curva);
				PruebaSimboloLegendre.imprimirTiempoEstimado(m2);
				
				break;
			
			// METODO 3: Escaneo del rango
			case 3:
				PruebaEscaneoRango.MetricasEscaneoRango m3 = PruebaEscaneoRango.estimarTiempo(curva);
				PruebaEscaneoRango.imprimirTiempoEstimado(m3);

				break;

			default:
				System.out.print("Numero de metodo no valido");
		}
	}
}
