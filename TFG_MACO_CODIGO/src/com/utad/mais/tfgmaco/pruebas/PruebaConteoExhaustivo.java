package com.utad.mais.tfgmaco.pruebas;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import com.utad.mais.tfgmaco.curva.CurvaElipticaFp;
import com.utad.mais.tfgmaco.operaciones.AritmeticaModular;

public class PruebaConteoExhaustivo {
	
	// Metricas para la prueba
	public static class MetricasConteoExhaustivo {
		public double tiempoIteracionMs;
		public long iteracionesReales;
		public BigInteger iteracionesMaximas;
		public BigDecimal estimacionTotalMs;
		public BigInteger puntosEncontrados;
	}
	
	public static MetricasConteoExhaustivo estimarTiempo(CurvaElipticaFp curva) {
		MetricasConteoExhaustivo metricas = new MetricasConteoExhaustivo();
		
		// Informacion de la curva
		BigInteger a = curva.getA();
		BigInteger b = curva.getB();
		BigInteger modulo = curva.getModulo();
		BigInteger numeroPuntos = BigInteger.ONE;
		
		// Por equivalencias modulares, solo probamos valores de y hasta (p-1)/2
		BigInteger limiteY = modulo.subtract(BigInteger.ONE).divide(BigInteger.TWO);
				
		
		// MEDICION DURANTE 90 SEGUNDOS
		System.out.println("\nMidiendo el proceso durante 90 segundos...");
		
		long tInicio = System.nanoTime();
		long duracionNs = 90000000000L;
		BigInteger x = BigInteger.ZERO;
		metricas.iteracionesReales = 0;
		
		// Recorremos todos los posibles valores de x desde 0 hasta modulo-1 hasta que se acaben los 90 segundos
		while (System.nanoTime() - tInicio < duracionNs && x.compareTo(modulo) < 0) {
			
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
					} else {
						numeroPuntos = numeroPuntos.add(BigInteger.TWO);					
					}						
					
					break;
				}
			}
			
			x = x.add(BigInteger.ONE);
            metricas.iteracionesReales++;
		}
		
		
		// ESTIMACIONES
		
		if (metricas.iteracionesReales == 0) {
			System.out.println("No se ha podido medir ninguna iteracion");
		}
		
		long tFin = System.nanoTime();
		
		// Calculamos la estimacion de la iteracion en milisegundos
		double tiempoTotalMs = (tFin - tInicio) / 1000000.0;
		metricas.tiempoIteracionMs = tiempoTotalMs / metricas.iteracionesReales;
		
		// Si termina el calculo antes de que se acaben los 90 segundos
		if (x.compareTo(modulo) >= 0) {
			System.out.println("Algoritmo terminado durante la medicion");
			metricas.iteracionesMaximas = modulo;
			metricas.estimacionTotalMs = BigDecimal.valueOf(tiempoTotalMs);
			
			return metricas;
		}
			
		
		// EXTRAPOLACION
		
		metricas.iteracionesMaximas = modulo;
		metricas.puntosEncontrados = numeroPuntos;
		BigDecimal tiempoMedicionMs = BigDecimal.valueOf(tiempoTotalMs);
		
		// Calculamos las iteraciones faltantes
		BigInteger iteracionesRestantes = modulo.subtract(BigInteger.valueOf(metricas.iteracionesReales));
		
		// El tiempo restante sera lo que tarda de media cada iteracion por las iteraciones faltantes
		BigDecimal tIteracion = BigDecimal.valueOf(metricas.tiempoIteracionMs);
		BigDecimal tiempoRestanteMs = tIteracion.multiply(new BigDecimal(iteracionesRestantes));
		
		// El tiempo total es lo que ha tardado la medicion + lo que se estima que falta
        metricas.estimacionTotalMs = tiempoMedicionMs.add(tiempoRestanteMs);
        
        return metricas;
	}
	
	public static void imprimirTiempoEstimado(MetricasConteoExhaustivo metricas) {
		System.out.println("\n===== ESTIMACION METODO CONTEO EXHAUSTIVO =====\n");
		
		System.out.printf("Iteraciones evaluadas: %d %n", metricas.iteracionesReales);
		System.out.printf("Tiempo medio por iteracion: %.5f ms %n", metricas.tiempoIteracionMs);
		System.out.printf("Iteraciones maximas (modulo): %d %n", metricas.iteracionesMaximas);
		System.out.printf("Numero de puntos calculados: %d %n", metricas.puntosEncontrados);
		
		// Impresion del tiempo total estimado en varias unidades
		BigDecimal ms = metricas.estimacionTotalMs;
		BigDecimal s = ms.divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);
		BigDecimal horas = s.divide(new BigDecimal("3600"), 2, RoundingMode.HALF_UP);
		BigDecimal dias = horas.divide(new BigDecimal("24"), 2, RoundingMode.HALF_UP);
		BigDecimal anios = dias.divide(new BigDecimal("365"), 2, RoundingMode.HALF_UP);
		
		System.out.println("\nTiempo total estimado:");
		System.out.printf("Años: %.2f  -  Dias: %.2f  -  Horas: %.2f  -  Segundos: %.2f  -  Milisegundos: %.2f%n",
				anios.doubleValue(), dias.doubleValue(), horas.doubleValue(), s.doubleValue(), ms.doubleValue());
	}
}