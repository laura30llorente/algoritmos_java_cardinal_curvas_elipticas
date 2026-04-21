package com.utad.mais.tfgmaco.algoritmos;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

import com.utad.mais.tfgmaco.curva.CurvaElipticaFp;
import com.utad.mais.tfgmaco.operaciones.AritmeticaModular;
import com.utad.mais.tfgmaco.operaciones.DoubleAndAdd;
import com.utad.mais.tfgmaco.polinomios.OperacionesPuntoPolinomico;
import com.utad.mais.tfgmaco.polinomios.Polinomio;
import com.utad.mais.tfgmaco.polinomios.PolinomioDivision;
import com.utad.mais.tfgmaco.polinomios.PuntoProyectivoPolinomico;
import com.utad.mais.tfgmaco.polinomios.ElementoExtensionPolinomica;

public class AlgoritmoSchoof {

	public static BigInteger contarPuntos(CurvaElipticaFp curva) {
		
		BigInteger numeroPuntos = BigInteger.ONE;   // Contamos el punto en el infinito
		
		BigInteger modulo = curva.getModulo();
		BigInteger a = curva.getA();
		BigInteger b = curva.getB();
		
		// Calculamos el polinomio de la curva
		Polinomio curvaPolinomio = calcularCurvaPolinomio(a, b, modulo);
		
		// Obtenemos los primos dentro del intervalo de Hasse
		ArrayList<BigInteger> listaPrimos = eleccionPrimos(modulo);
		ArrayList<BigInteger> candidatosT = new ArrayList<BigInteger>();
		
		// Polinomios de utilidad
		Polinomio cero = Polinomio.crearMonomio(curva.getModulo(), 0, BigInteger.ZERO);
		Polinomio uno = Polinomio.crearMonomio(curva.getModulo(), 0, BigInteger.ONE);
		Polinomio x = Polinomio.crearMonomio(curva.getModulo(), 1, BigInteger.ONE);
		
		// Otras inicializaciones
		BigInteger l = BigInteger.ZERO;
		BigInteger candidato = BigInteger.ZERO;
		
		PolinomioDivision.limpiarHistorial();
		PolinomioDivision.calcularFCuadrado(curvaPolinomio);
		
		// Por cada primo de la lista, calculamos el candidato de la traza de Frobenius t
		for (int i = 0; i < listaPrimos.size(); i++) {
			long tiempoInicio = System.currentTimeMillis();
			
			l = listaPrimos.get(i);
			
			// Schoof distingue un procedimiento diferente cuando l=2
			if (l.equals(BigInteger.TWO)) {
				
				candidato = obtenerTMod2(x, curvaPolinomio, modulo);
				candidatosT.add(candidato);
				
			} else {
				
				// Polinomio division de grado l
				Polinomio polinomioDiv = PolinomioDivision.calcularPolinomioDivision(l, modulo, curva);
				
				
				// Pi_p = (X_1, Y_1 * y)
				Polinomio X_1 = calcularX_1(x, modulo, polinomioDiv);
				Polinomio Y_1 = calcularY_1(curvaPolinomio, modulo, polinomioDiv);
				
				// (Pi_p)^2 = (X_2, Y_2 * y)
				Polinomio X_2 = calcularX_2(X_1, modulo, polinomioDiv);			
				Polinomio Y_2 = calcularY_2(Y_1, modulo, polinomioDiv);
				
				
				// ECUACION CARACTERISTICA DE FROBENIUS -> (Pi_p)^2(P) + pP = t * Pi_p(P)
				
				// --- LADO IZQUIERDO ---
				
				// Elemento de extension correspondiente al numero 1
				ElementoExtensionPolinomica zPi = ElementoExtensionPolinomica.elementoUno(modulo, curvaPolinomio, polinomioDiv);
				
				// Con X_2 e Y_2, formamos el punto polinomico (Pi_p)^2 = (X_2, Y_2 * y)
				PuntoProyectivoPolinomico Pi_pCuadrado = formarPunto(X_2, Y_2, modulo, cero, polinomioDiv, curvaPolinomio, zPi);
								
				// Creamos el punto polinomico P = (x, y)
				PuntoProyectivoPolinomico P = formarPunto(x, uno, modulo, cero, polinomioDiv, curvaPolinomio, zPi);


				// Calculamos p
				BigInteger pModl = modulo.mod(l);
				
				// Calculamos el producto pP
				PuntoProyectivoPolinomico pP = DoubleAndAdd.algoritmoDoubleAddProyectivo(pModl, P, polinomioDiv, curva, curvaPolinomio);

				// Sumamos ambas partes del lado izquierdo -> (Pi_p)^2(P) + pP
				PuntoProyectivoPolinomico ladoIzquierdo = OperacionesPuntoPolinomico.sumarPuntosPolinomicosProyectivos(Pi_pCuadrado, pP, polinomioDiv, curva, curvaPolinomio);

				
				// --- LADO DERECHO ---
				
				// Con X_1 e Y_1, formamos el punto polinomico Pi_p = (X_1, Y_1 * y)
				PuntoProyectivoPolinomico Pi_p = formarPunto(X_1, Y_1, modulo, cero, polinomioDiv, curvaPolinomio, zPi);
				
				BigInteger tModl = BigInteger.ZERO;
		
				// La busqueda llega hasta (l+1)/2 porque la otra mitad viene de la relacion 'opuestos'
				BigInteger limite = l.add(BigInteger.ONE).divide(BigInteger.TWO);
				
				PuntoProyectivoPolinomico ladoDerecho = Pi_p;
				
				// Bucle que busca el valor de t probando desde 0 hasta limite
				for (BigInteger t = BigInteger.ONE; t.compareTo(limite) <= 0; t = t.add(BigInteger.ONE)) {
					
					// 0 -> opuestos,  1 -> iguales,  2 -> distintos
					int relacion = OperacionesPuntoPolinomico.relacionPuntos(ladoDerecho, ladoIzquierdo);
					
					if (relacion == 1) {   // iguales
						tModl = t;
						break;
						
					} else if (relacion == 0) {    // opuestos
						tModl = AritmeticaModular.restar(l, t, l);
						break;
					}
					
					// Si no se ha encontrado t, probamos sumando Pi_p al ladoDerecho para avanzar
					ladoDerecho = OperacionesPuntoPolinomico.sumarPuntosPolinomicosProyectivos(ladoDerecho, Pi_p, polinomioDiv, curva, curvaPolinomio);
				}
				
				candidatosT.add(tModl);
			}
			
			long tiempoFin = System.currentTimeMillis();
	        double segundos = (tiempoFin - tiempoInicio) / 1000.0;
	        int gradoPsi = l.equals(BigInteger.TWO) ? 3 : (l.pow(2).intValue() - 1) / 2;
	        
	        System.out.printf("\n[%d/%d] Primo l = %d (Grado psi_l = %d) -> Tiempo: %.2f s%n",
	                (i + 1), listaPrimos.size(), l, gradoPsi, segundos, segundos / 60.0);
		}

		// Calculamos el producto M de los primos
		BigInteger M = calcularProductoM(listaPrimos);
		
		// Aplicamos el Teorema Chino del Resto para obtener t dentro del intervalo
		BigInteger t = teoremaChinoResto(M, listaPrimos, candidatosT);
		
		// Comprobamos que t esta dentro del intervalo de Hasse, realizando el modulo si es necesario
		t = comprobarIntervaloHasse(t, M, modulo);
		
		
		// Por ultimo, calculamos el numero de puntos con la formula #E(F_p) = p + 1 - t
		numeroPuntos = modulo.add(BigInteger.ONE).subtract(t);
		
		return numeroPuntos;
	}
	
	// Eleccion de primos distintos de p tal que su producto sea mayor que 4 * sqrt(p)
	public static ArrayList<BigInteger> eleccionPrimos(BigInteger modulo) {
		ArrayList<BigInteger> listaPrimos = new ArrayList<BigInteger>();
		BigInteger primo = BigInteger.TWO;    // El primer primo es el dos
		BigInteger producto = BigInteger.ONE;
		
		while (producto.compareTo(calcularLimiteSuperior(modulo).multiply(BigInteger.valueOf(2))) <= 0) {
			
			// Añadimos el primo y realizamos el nuevo producto
			listaPrimos.add(primo);
			producto = producto.multiply(primo);
			
			// Buscamos el siguiente numero primo
			primo = primo.nextProbablePrime();   // nextProbablePrime es mas eficiente que un bucle while manual
		}
		
		return listaPrimos;
	}
	
	// Limite inferior del intervalo: -2 * sqrt(p)
	public static BigInteger calcularLimiteInferior(BigInteger modulo) {
		
		BigDecimal moduloDecimal = new BigDecimal(modulo);
		MathContext mc = new MathContext(10);
		BigDecimal raiz = moduloDecimal.sqrt(mc);
		
		BigDecimal multiplicacion = raiz.multiply(BigDecimal.valueOf(2).negate());
		
		// Redondeamos al numero mas cercano dependiendo de los decimales
		BigDecimal limiteInferior = multiplicacion.setScale(0, RoundingMode.HALF_UP);
		
		return limiteInferior.toBigIntegerExact();
	}
	
	// Limite superior del intervalo: 2 * sqrt(p)
	public static BigInteger calcularLimiteSuperior(BigInteger modulo) {
		
		BigDecimal moduloDecimal = new BigDecimal(modulo);
		MathContext mc = new MathContext(10);
		BigDecimal raiz = moduloDecimal.sqrt(mc);
		
		BigDecimal multiplicacion = raiz.multiply(BigDecimal.valueOf(2));
		
		// Redondeamos al numero mas cercano dependiendo de los decimales
		BigDecimal limiteSuperior = multiplicacion.setScale(0, RoundingMode.HALF_UP);
		
		return limiteSuperior.toBigIntegerExact();
	}
	
	public static Polinomio calcularCurvaPolinomio(BigInteger a, BigInteger b, BigInteger modulo) {
		// Calculamos el polinomio de la curva
		ArrayList<BigInteger> coeficientesCurva = new ArrayList<BigInteger>();
		coeficientesCurva.add(b);
		coeficientesCurva.add(a);
		coeficientesCurva.add(BigInteger.ZERO);
		coeficientesCurva.add(BigInteger.ONE);
		
		return new Polinomio(modulo, coeficientesCurva);
	}
	
	public static BigInteger obtenerTMod2(Polinomio x, Polinomio curva, BigInteger modulo) {

		// Calculamos x^p - x
		Polinomio xpMod = Polinomio.exponenciar(x, modulo, curva);
		Polinomio resta = Polinomio.restar(xpMod, x); 

		// Realizamos el mcd de ambos
		Polinomio mcd = Polinomio.mcd(resta, curva);

		// Si hay raiz, entonces hay 2-torsion -> t ≡ 0 (mod 2)
		// Si no, t ≡ 1 (mod 2)
		BigInteger tMod2 = (mcd.gradoPolinomio() > 0) ? BigInteger.ZERO : BigInteger.ONE; 
				
		return tMod2;
	}
	
	public static Polinomio calcularX_1(Polinomio x, BigInteger modulo, Polinomio polinomioDivision) {
		// X_1 = x^p mod psi_l
		Polinomio xpMod = Polinomio.exponenciar(x, modulo, polinomioDivision);

		return xpMod;
	}
	
	public static Polinomio calcularY_1(Polinomio curva, BigInteger modulo, Polinomio polinomioDivision) {
		// Y_1 = f^(p-1/2) mod psi_l
		BigInteger exponente = modulo.subtract(BigInteger.ONE).divide(BigInteger.TWO);
		Polinomio fExponenteMod = Polinomio.exponenciar(curva, exponente, polinomioDivision);
		
		return fExponenteMod;
	}
	
	public static Polinomio calcularX_2(Polinomio X_1, BigInteger modulo, Polinomio polinomioDivision) {
		// X_2 = (X_1)^p mod psi_l
		Polinomio X_1pMod = Polinomio.exponenciar(X_1, modulo, polinomioDivision);
		
		return X_1pMod;
	}
	
	public static Polinomio calcularY_2(Polinomio Y_1, BigInteger modulo, Polinomio polinomioDivision) {
		// Y_2 = (Y_1)^p * Y_1 mod psi_l
		Polinomio Y_1pMod = Polinomio.exponenciar(Y_1, modulo, polinomioDivision);
		Polinomio Y_1pY_1 = Polinomio.multiplicar(Y_1pMod, Y_1);
		Polinomio Y_1pY_1Mod = Polinomio.dividir(Y_1pY_1, polinomioDivision)[1];
		
		return Y_1pY_1Mod;
	}
	
	public static PuntoProyectivoPolinomico formarPunto(
			Polinomio factorX, Polinomio factorY, BigInteger modulo, Polinomio cero, 
			Polinomio polinomioDiv, Polinomio curvaPolinomio, ElementoExtensionPolinomica elementoExtensionUno) {
		
		// Va a ser (factorX + 0), es decir, (X_1 + 0) para Pi_p,   (X_2 + 0) para (Pi_p)^2  y  (x + 0) para P
		ElementoExtensionPolinomica xPi = new ElementoExtensionPolinomica(factorX, cero, curvaPolinomio, polinomioDiv);
		
		// Va a ser (0 + factorY * y), es decir, (0 + Y_1 * y) para Pi_p,   (0 + Y_2 * y) para (Pi_p)^2  y  (0 + 1 * y) para P
		ElementoExtensionPolinomica yPi = new ElementoExtensionPolinomica(cero, factorY, curvaPolinomio, polinomioDiv);
		
		// Va a ser siempre 1
		ElementoExtensionPolinomica zPi = elementoExtensionUno;
		
		// Formamos el punto polinomico Pi_p = (X_1, Y_1 * y, 1) o (Pi_p)^2 = (X_2, Y_2 * y, 1) o P = (x, y, 1)
		return new PuntoProyectivoPolinomico(xPi, yPi, zPi);
	}
	
	public static BigInteger calcularProductoM(ArrayList<BigInteger> listaPrimos) {
		// Calculamos el producto M de los primos
		
		BigInteger M = BigInteger.ONE;
		
		for (int i = 0; i < listaPrimos.size(); i++) {
			M = M.multiply(listaPrimos.get(i));
		}
		
		return M;
	}
	
	public static BigInteger teoremaChinoResto(BigInteger M, ArrayList<BigInteger> listaPrimos, ArrayList<BigInteger> candidatosT) {
		
		// Calculamos los inversos
		
		BigInteger M_i = BigInteger.ONE;
		BigInteger inverso_i = BigInteger.ONE;
		ArrayList<BigInteger> listaInversos = new ArrayList<BigInteger>();
		ArrayList<BigInteger> listaDivisiones = new ArrayList<BigInteger>();
		
		for (int i = 0; i < listaPrimos.size(); i++) {
			// Calculamos M_i = M / primo_i
			M_i = M.divide(listaPrimos.get(i));
			
			// Calculamos los inversos de M_i (mod primo_i)
			inverso_i = M_i.modInverse(listaPrimos.get(i));
			
			// Guardamos todas las divisiones y todos los inversos
			listaDivisiones.add(M_i);
			listaInversos.add(inverso_i);
		}
		
		// Calculamos el valor de t como la suma de todos los tModl_i * M_i * inverso_i (mod M)

		BigInteger t = BigInteger.ZERO;
		BigInteger tModl_i = BigInteger.ONE;
		
		for (int i = 0; i < listaPrimos.size(); i++) {
			tModl_i = candidatosT.get(i);
			M_i = listaDivisiones.get(i);
			inverso_i = listaInversos.get(i);

			t = t.add(tModl_i.multiply(M_i).multiply(inverso_i));
		}
		
		return t.mod(M);
	}
	
	// Al final del procedimiento, solo debe quedar un candidato t, que se trata de la traza de Frobenius
	// Se comprueba para mayor seguridad
	public static BigInteger comprobarIntervaloHasse(BigInteger t, BigInteger M, BigInteger modulo) {
		BigInteger limiteSuperior = calcularLimiteSuperior(modulo);
		BigInteger limiteInferior = calcularLimiteInferior(modulo);
		
		// El modulo realizado en teoremaChinoResto() va a dejar el valor de t entre 0 y M-1, pero nunca negativo
		// Por lo tanto, si t sigue superando el limite superior, tenemos que restarle el valor de M manualmente
		if (t.compareTo(limiteSuperior) > 0) {
			t = t.subtract(M);
		}
		
		// Ahora, t estaria incorrectamente calculado si fuera menor que el limite inferior o mayor que el limite superior
		if (t.compareTo(limiteInferior) < 0 || t.compareTo(limiteSuperior) > 0) {
			throw new ArithmeticException("t no se encuentra dentro del intervalo de Hasse");
		}
		
		return t;
	}
}

