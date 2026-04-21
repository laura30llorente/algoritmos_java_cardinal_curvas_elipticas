package com.utad.mais.tfgmaco.polinomios;

import java.math.BigInteger;
import java.util.HashMap;

import com.utad.mais.tfgmaco.curva.CurvaElipticaFp;

public class OperacionesPuntoPolinomico {
	public static HashMap<String, ElementoExtensionPolinomica> calcularVariables(PuntoProyectivoPolinomico P, PuntoProyectivoPolinomico Q) {
		// Obtenemos las coordenadas de los puntos
		ElementoExtensionPolinomica X_1 = P.getX();
		ElementoExtensionPolinomica Y_1 = P.getY();
		ElementoExtensionPolinomica Z_1 = P.getZ();
		ElementoExtensionPolinomica X_2 = Q.getX();
		ElementoExtensionPolinomica Y_2 = Q.getY();
		ElementoExtensionPolinomica Z_2 = Q.getZ();
		
		
		// U_1 = X_1 * (Z_2)^2
		ElementoExtensionPolinomica Z_2Cuadrado = Z_2.exponenciar(BigInteger.TWO);
		ElementoExtensionPolinomica U_1 = X_1.multiplicar(Z_2Cuadrado);
		
		// U_2 = X_2 * (Z_1)^2
		ElementoExtensionPolinomica Z_1Cuadrado = Z_1.exponenciar(BigInteger.TWO);
		ElementoExtensionPolinomica U_2 = X_2.multiplicar(Z_1Cuadrado);
				
		// S_1 = Y_1 * (Z_2)^3
		ElementoExtensionPolinomica Z_2Cubo = Z_2Cuadrado.multiplicar(Z_2);
		ElementoExtensionPolinomica S_1 = Y_1.multiplicar(Z_2Cubo);
				
		// S_2 = Y_2 * (Z_1)^3
		ElementoExtensionPolinomica Z_1Cubo = Z_1Cuadrado.multiplicar(Z_1);
		ElementoExtensionPolinomica S_2 = Y_2.multiplicar(Z_1Cubo);
				
		// H = U_2 - U_1
		ElementoExtensionPolinomica H = U_2.restar(U_1);
		
		// R = S_2 - S_1
		ElementoExtensionPolinomica R = S_2.restar(S_1);
		
		
		// Lo devolemos todo junto en un HashMap
		HashMap<String, ElementoExtensionPolinomica> variables = new HashMap<String, ElementoExtensionPolinomica>();
		variables.put("X_1", X_1);
		variables.put("Y_1", Y_1);
		variables.put("Z_1", Z_1);
		variables.put("X_2", X_2);
		variables.put("Y_2", Y_2);
		variables.put("Z_2", Z_2);
		variables.put("U_1", U_1);
		variables.put("U_2", U_2);
		variables.put("S_1", S_1);
		variables.put("S_2", S_2);
		variables.put("H", H);
		variables.put("R", R);
		
		return variables;
	}
	
	// Metodo para elegir el caso de suma de puntos
	public static PuntoProyectivoPolinomico sumarPuntosPolinomicosProyectivos(
			PuntoProyectivoPolinomico P, PuntoProyectivoPolinomico Q, Polinomio polinomioDiv, 
			CurvaElipticaFp curva, Polinomio curvaPolinomio) {
		
		// Comprobamos que los puntos no son null
		if (P == null || Q == null) {
			throw new IllegalArgumentException("Puntos no validos");
		}
		
		// Por la propiedad algebraica de elemento neutro: P + PuntoInfinito = P
		if (P.esPuntoInfinito()) {
			return Q;
		}
		if (Q.esPuntoInfinito()) {
			return P;
		}
		
		HashMap<String, ElementoExtensionPolinomica> variables = calcularVariables(P, Q);
		
		// CASOS
		
		if (variables.get("H").esCero()) {
			if (variables.get("R").esCero()) {
				// Puntos iguales -> P == Q
				
				return sumaPuntosDuplicacionProy(variables.get("X_1"), variables.get("Y_1"), variables.get("Z_1"), curva);
			} else {
				// Puntos opuestos -> P == -Q -> su suma sera el punto en el infinito
				
				return new PuntoProyectivoPolinomico(curva.getModulo(), curvaPolinomio, polinomioDiv);
			}
		}
		
		// Puntos distintos -> P != Q
		return sumaPuntosDistintosProy(variables.get("H"), variables.get("R"), variables.get("U_1"), variables.get("S_1"), variables.get("Z_1"), variables.get("Z_2"));
		
	}
	
	// CASO 1: PUNTOS DISTINTOS
	public static PuntoProyectivoPolinomico sumaPuntosDistintosProy(
			ElementoExtensionPolinomica H, ElementoExtensionPolinomica R, ElementoExtensionPolinomica U_1,
			ElementoExtensionPolinomica S_1, ElementoExtensionPolinomica Z_1, ElementoExtensionPolinomica Z_2) {
		
		// X_3 = -H^3 - 2 * U_1 * H^2 + R^2 
				
		ElementoExtensionPolinomica HCuadrado = H.exponenciar(BigInteger.TWO);
		
		ElementoExtensionPolinomica U_1HCuadrado = U_1.multiplicar(HCuadrado);
		ElementoExtensionPolinomica XTermino2 = U_1HCuadrado.multiplicarConstante(BigInteger.TWO);
		
		ElementoExtensionPolinomica XTermino1 = HCuadrado.multiplicar(H);
		
		ElementoExtensionPolinomica XTermino3 = R.exponenciar(BigInteger.TWO);
		
		ElementoExtensionPolinomica X_3 = XTermino3.restar(XTermino2).restar(XTermino1);
		
		
		// Y_3 = -S_1 * H^3 + R * (U_1 * H^2 - X_3)
		
		ElementoExtensionPolinomica YTermino1 = S_1.multiplicar(XTermino1);
		
		ElementoExtensionPolinomica restaParentesis = U_1HCuadrado.restar(X_3);
		ElementoExtensionPolinomica YTermino2 = R.multiplicar(restaParentesis);
		
		ElementoExtensionPolinomica Y_3 = YTermino2.restar(YTermino1);
		
		
		// Z_3 = Z_1 * Z_2 * H
		
		ElementoExtensionPolinomica productoZ_3 = Z_1.multiplicar(Z_2);
		ElementoExtensionPolinomica Z_3 = productoZ_3.multiplicar(H);
		
		
		// Punto proyectivo = (X_3, Y_3, Z_3)
		return new PuntoProyectivoPolinomico(X_3, Y_3, Z_3);
	}
	
	// CASO 2: PUNTOS COINCIDENTES
	public static PuntoProyectivoPolinomico sumaPuntosDuplicacionProy(
			ElementoExtensionPolinomica X_1, ElementoExtensionPolinomica Y_1, 
			ElementoExtensionPolinomica Z_1, CurvaElipticaFp curva) {
		
		// S = 4 * X_1 * (Y_1)^2
		
		ElementoExtensionPolinomica productoS = X_1.multiplicarConstante(BigInteger.valueOf(4));
		ElementoExtensionPolinomica Y_1Cuadrado = Y_1.exponenciar(BigInteger.TWO);
		
		ElementoExtensionPolinomica S = productoS.multiplicar(Y_1Cuadrado);
		
		// M = 3 * (X_1)^2 + A * (Z_1)^4 
		
		ElementoExtensionPolinomica X_1Cuadrado = X_1.exponenciar(BigInteger.TWO);
		ElementoExtensionPolinomica MTermino1 = X_1Cuadrado.multiplicarConstante(BigInteger.valueOf(3));
		
		ElementoExtensionPolinomica Z_1Cuadrado = Z_1.exponenciar(BigInteger.TWO);
		ElementoExtensionPolinomica Z_1Cuarta = Z_1Cuadrado.exponenciar(BigInteger.TWO);
		ElementoExtensionPolinomica MTermino2 = Z_1Cuarta.multiplicarConstante(curva.getA());
		
		ElementoExtensionPolinomica M = MTermino1.sumar(MTermino2);
		
		// T = - 2 * S + M^2
		
		ElementoExtensionPolinomica TTermino1 = S.multiplicarConstante(BigInteger.TWO);
		
		ElementoExtensionPolinomica TTermino2 = M.exponenciar(BigInteger.TWO);
		
		ElementoExtensionPolinomica T = TTermino2.restar(TTermino1);
		
		
		// X_3 = T
		
		ElementoExtensionPolinomica X_3 = T;
		
		
		// Y_3 = - 8 * (Y_1)^4 + M * (S - T)
		
		ElementoExtensionPolinomica Y_1Cuarta = Y_1Cuadrado.exponenciar(BigInteger.TWO);
		ElementoExtensionPolinomica YTermino1 = Y_1Cuarta.multiplicarConstante(BigInteger.valueOf(8));
	
		ElementoExtensionPolinomica restaParentesis = S.restar(T);
		ElementoExtensionPolinomica YTermino2 = M.multiplicar(restaParentesis);
		
		ElementoExtensionPolinomica Y_3 = YTermino2.restar(YTermino1);
		
		
		// Z_3 = 2 * Y_1 * Z_1
		
		ElementoExtensionPolinomica productoZ_3 = Y_1.multiplicar(Z_1);
		ElementoExtensionPolinomica Z_3 = productoZ_3.multiplicarConstante(BigInteger.TWO);
		
		// Punto proyectivo = (X_3, Y_3, Z_3)
		return new PuntoProyectivoPolinomico(X_3, Y_3, Z_3);
	}
	
	
	// Metodo para determinar la relacion entre los puntos
	public static int relacionPuntos(PuntoProyectivoPolinomico P, PuntoProyectivoPolinomico Q) {
		// 0 -> opuestos
		// 1 -> iguales
		// 2 -> distintos
		
		if (P.esPuntoInfinito() && Q.esPuntoInfinito()) {
			return 1;
		}
		
		if (P.esPuntoInfinito() || Q.esPuntoInfinito()) {
			return 2;
		}
		
		// Obtenemos todos los valores necesarios
		HashMap<String, ElementoExtensionPolinomica> variables = calcularVariables(P, Q);
		
		// Si las X son distintas
		if (!variables.get("H").esCero()) {
			return 2;
		}
			
		// Si las X son iguales
		if (variables.get("R").esCero()) {
			return 1;
		} else {         // Si las X son opuestas
			return 0;
		}
	}
}
