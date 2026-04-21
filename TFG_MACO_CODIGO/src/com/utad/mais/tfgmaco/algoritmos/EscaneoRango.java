package com.utad.mais.tfgmaco.algoritmos;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

import com.utad.mais.tfgmaco.curva.CurvaElipticaFp;
import com.utad.mais.tfgmaco.operaciones.DoubleAndAdd;
import com.utad.mais.tfgmaco.punto.PuntoFp;

public class EscaneoRango {
	public static BigInteger contarPuntos(CurvaElipticaFp curva) {
		
		BigInteger modulo = curva.getModulo();
		
		// Encontramos primer punto P que cumpla la ecuacion de la curva
		PuntoFp puntoP = encontrarPunto(curva, BigInteger.ZERO, BigInteger.ZERO);
		
		// Calculamos m_0 = ⌈(sqrt(p)-1)^2⌉
		BigInteger m0 = calcularM0(modulo);
		BigInteger escalarP = m0;
		
		// Double and add a partir de m_0 * P hasta encontrar un m tal que m * P = punto infinito
		while (!DoubleAndAdd.algoritmoDoubleAddAfin(puntoP, escalarP).equals(curva.getPuntoInfinto())) {
			escalarP = escalarP.add(BigInteger.ONE);
		}
		
		// Calculamos el orden de P
		BigInteger ordenP = encontrarOrdenPunto(escalarP, curva, puntoP);
		
		// Numero de puntos debe pertenecer al intervalo de Hasse
		BigInteger inferiorHasse = calcularHasseInferior(modulo);
		BigInteger superiorHasse = calcularHasseSuperior(modulo);
		
		// Calculamos multiplos de |P| dentro del intervalo
		ArrayList<BigInteger> candidatos = multiplosOrdenPunto(ordenP, inferiorHasse, superiorHasse);

		PuntoFp puntoBase = puntoP;
		
		// Para evitar bucles infinitos cuando el metodo de escaneo del rango no consigue reducir a un unico candidato
		// Puede ocurrir cuando los nuevos puntos tienen ordenes que siguen dividiendo m
		int intentos = 0;
		int maximoIntentos = 20;
		
		// Si existen multiplos de |P| dentro del intervalo
		// Repetimos el procedimiento con puntos cuya combinacion (m.c.m. de ordenes) sea un multiplo unico
		while (candidatos.size() > 1 && intentos < maximoIntentos) {
			// Nuevo punto distinto que el punto base
			// Como el inverso -P comparte la misma X y tiene el mismo orden, probamos directamente con la siguiente abscisa (x + 1, 0)
			PuntoFp puntoQ = encontrarPunto(curva, puntoBase.getX().add(BigInteger.ONE), BigInteger.ZERO);
			if (puntoQ == null) {
				break;
			}
			puntoBase = puntoQ;
			
			// Reusamos m_0
			BigInteger escalarQ = m0;
			
			// Double and add a partir de m_0 * Q hasta encontrar un m tal que m * Q = punto infinito
			while (!DoubleAndAdd.algoritmoDoubleAddAfin(puntoQ, escalarQ).equals(curva.getPuntoInfinto())) {
				escalarQ = escalarQ.add(BigInteger.ONE);
			}
			
			// Calculamos el orden de Q
			BigInteger ordenQ = encontrarOrdenPunto(escalarQ, curva, puntoQ);
			
			// Combinamos los ordenes
			BigInteger combinacionOrdenes = mcm(ordenP, ordenQ);

			// Obtenemos los multiplos del orden de la combinacion
			candidatos = multiplosOrdenPunto(combinacionOrdenes, inferiorHasse, superiorHasse);
			
			intentos++;
		}
		
		if (candidatos.size() == 1) {
			// Devolvemos el numero de puntos de la curva
			return candidatos.get(0);
		} else {
			// Delegamos a uno de los metodos previos
			System.out.println("Delegacion del calculo al metodo de Legendre");
			return MetodoLegendre.contarPuntos(curva);
		}
		
	}

	public static PuntoFp encontrarPunto(CurvaElipticaFp curva, BigInteger xInicio, BigInteger yInicio) {
		
		BigInteger x = xInicio;
		
		while (x.compareTo(curva.getModulo()) < 0) {
			
			// Si x ya no es el valor inicial, y pasa a valer 0 para comprobar la siguiente tanda de puntos
			BigInteger y = (x.equals(xInicio)) ? yInicio.mod(curva.getModulo()) : BigInteger.ZERO;
			
			while (y.compareTo(curva.getModulo()) < 0 && !curva.perteneceCurva(x, y)) {
				y = y.add(BigInteger.ONE);
			}
			
			// Si ha salido del bucle while por la segunda condicion, es que el punto pertenece a la curva
			if (y.compareTo(curva.getModulo()) < 0) {
				return new PuntoFp(x, y, curva);
			}
			
			x = x.add(BigInteger.ONE);
		}
		
		return null;
	}
	
	public static BigInteger calcularM0(BigInteger modulo) {
		/*
		 * Problema:
		 * 	sqrt() de BigInteger elimina los decimales y sqrtAndReminder() devuelve el resto pero no los decimales
		 * 	Necesitamos los decimales de la raiz para el correcto calculo de m_0 = ⌈(sqrt(p)-1)^2⌉
		 */
				
		// Transformamos el modulo a BigDecimal para calcular la raiz y mantener los decimales
		BigDecimal moduloDecimal = new BigDecimal(modulo);
		MathContext mc = new MathContext(100);
		BigDecimal raiz = moduloDecimal.sqrt(mc);
		
		BigDecimal resta = raiz.subtract(BigDecimal.ONE);
		BigDecimal cuadrado = resta.multiply(resta);
		
		// Funcion techo
		BigDecimal m0 = cuadrado.setScale(0, RoundingMode.CEILING);
		
		return m0.toBigIntegerExact();   // Transformamos de vuelta a BigInteger
	}

	// Limite inferior del intervalo de Hasse: p + 1 - 2 * sqrt(p)
	public static BigInteger calcularHasseInferior(BigInteger modulo) {
		
		BigDecimal moduloDecimal = new BigDecimal(modulo);
		MathContext mc = new MathContext(100);
		BigDecimal raiz = moduloDecimal.sqrt(mc);
		
		BigDecimal multiplicacion = raiz.multiply(BigDecimal.valueOf(2));
		BigDecimal operacion = moduloDecimal.add(BigDecimal.ONE).subtract(multiplicacion);
		
		// Redondeamos al numero mas cercano dependiendo de los decimales
		BigDecimal limiteInferior = operacion.setScale(0, RoundingMode.HALF_UP);
		
		return limiteInferior.toBigIntegerExact();
	}
	
	// Limite superior del intervalo de Hasse: p + 1 + 2 * sqrt(p)
	public static BigInteger calcularHasseSuperior(BigInteger modulo) {
		
		BigDecimal moduloDecimal = new BigDecimal(modulo);
		MathContext mc = new MathContext(100);
		BigDecimal raiz = moduloDecimal.sqrt(mc);
		
		BigDecimal multiplicacion = raiz.multiply(BigDecimal.valueOf(2));
		BigDecimal operacion = moduloDecimal.add(BigDecimal.ONE).add(multiplicacion);
		
		// Redondeamos al numero mas cercano dependiendo de los decimales
		BigDecimal limiteSuperior = operacion.setScale(0, RoundingMode.HALF_UP);
		
		return limiteSuperior.toBigIntegerExact();
	}
	
	public static BigInteger encontrarOrdenPunto(BigInteger escalar, CurvaElipticaFp curva, PuntoFp punto) {
		
		boolean repetir = true;
		
		while (repetir) {
			repetir = false;
			
			// Factorizar m en primos: p_i
			ArrayList<BigInteger> factoresPrimosM = factorizarM(escalar);

			// Comprobar si se cumple (m / p_i) * P = punto infinito
			
				// Si se cumple -> se reemplaza m por m / p_i
				// Si no se cumple -> el orden de P es m
			
			int i = 0;
			PuntoFp puntoComprobar = new PuntoFp(BigInteger.ZERO, BigInteger.ZERO, curva);
			BigInteger division = BigInteger.ZERO;
			
			while (i < factoresPrimosM.size() && !puntoComprobar.esPuntoInfinito()) {
				division = escalar.divide(factoresPrimosM.get(i));
				puntoComprobar = DoubleAndAdd.algoritmoDoubleAddAfin(punto, division);
				i++;
			}
			
			// Si se ha salido del bucle porque el punto comprobado coincidia con el punto en el infinto
			if (i < factoresPrimosM.size()) {
				escalar = division;   // Esa division es la nueva m, y se repite el proceso de nuevo
				repetir = true;
			}
		}   // Al salir de este bucle, escalar sera el orden de P
		
		return escalar;
	}
	
	public static ArrayList<BigInteger> factorizarM(BigInteger m) {
		ArrayList<BigInteger> factoresPrimos = new ArrayList<BigInteger>();
		
		// Si m es par
		while (m.remainder(BigInteger.TWO).equals(BigInteger.ZERO)) {
			factoresPrimos.add(BigInteger.TWO);   // el 2 seria factor primo
			m = m.divide(BigInteger.TWO);
		}
		
		// Factores a partir del 3 -> a partir de aqui, m va a ser impar siempre
		// Sumamos 2 para saltar los pares y obtener solo los posibles factores impares
		for (BigInteger i = BigInteger.valueOf(3); i.multiply(i).compareTo(m) <= 0; i = i.add(BigInteger.TWO)) {  
			while (m.remainder(i).equals(BigInteger.ZERO)) {
				factoresPrimos.add(i);
				m = m.divide(i);
			}
		}
		
		// Si m es mayor que 1, implica que sigue quedando un primo
		if (m.compareTo(BigInteger.ONE) == 1) {
			factoresPrimos.add(m);
		}
		
		return factoresPrimos;
	}
	
	public static ArrayList<BigInteger> multiplosOrdenPunto(BigInteger ordenPunto, BigInteger inferiorHasse, BigInteger superiorHasse) {
		ArrayList<BigInteger> multiplosOrdenPunto = new ArrayList<BigInteger>();
		
		BigInteger multiplo = ordenPunto;
		BigInteger i = BigInteger.TWO;
		
		// Continuamos mientras no supere la cota superior del intervalo de Hasse
		while (multiplo.compareTo(superiorHasse) <= 0) {
			
			// Si esta dentro del intervalo de Hasse, es un multiplo valido
			if (multiplo.compareTo(inferiorHasse) >= 0) {
				multiplosOrdenPunto.add(multiplo);
			}
			
			// Probamos con el siguiente multiplo del orden del punto
			multiplo = ordenPunto.multiply(i);
			i = i.add(BigInteger.ONE);
		}
		
		return multiplosOrdenPunto;	
	}
	
	// Minimo comun multiplo usando la formula: MCM(a, b) = (a * b) / MCD(a, b)
	public static BigInteger mcm(BigInteger num1, BigInteger num2) {
		BigInteger mcm = BigInteger.ZERO;
		
		if (!num1.equals(BigInteger.ZERO) && !num2.equals(BigInteger.ZERO)) {
			mcm = num1.multiply(num2).divide(num1.gcd(num2));
		}
		
		return mcm;
	}
}
