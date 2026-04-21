package com.utad.mais.tfgmaco.polinomios;

import java.math.BigInteger;
import java.util.ArrayList;
import com.utad.mais.tfgmaco.operaciones.*;

public class Polinomio {
	private final BigInteger modulo;
	private final ArrayList<BigInteger> coeficientes;
	
	public Polinomio(BigInteger modulo, ArrayList<BigInteger> coeficientes) {
		this.modulo = modulo;
		this.coeficientes = new ArrayList<BigInteger>(coeficientes);
	}

	
	// Metodos getters
	public BigInteger getModulo() {
		return modulo;
	}

	public ArrayList<BigInteger> getCoeficientes() {
		return coeficientes;
	}
	
	
	// Funcion para calcular el grado de un polinomio
	public int gradoPolinomio() {
		return this.coeficientes.size() - 1;
 	}
	
	// Funcion para saber si el polinomio es el polinomio cero
	public boolean esCero() {
		return this.coeficientes.size() == 1 && this.coeficientes.get(0).equals(BigInteger.ZERO);
	}
	
	// Funcion para quitar los ceros que sobran en el polinomio
	public void limpiarPolinomio() {
		int pos = coeficientes.size() - 1;
		
		// Eliminamos los ceros, siempre dejando una posicion en el array de coeficientes
		while (coeficientes.size() > 1 && coeficientes.get(pos).equals(BigInteger.ZERO)) {
			coeficientes.remove(pos);
			pos--;
		}
 	}
	
	// Funcion para crear un monomio de la forma coeficienteMonomio * x^gradoMonomio
	public static Polinomio crearMonomio(BigInteger modulo, int gradoMonomio, BigInteger coeficienteMonomio) {
		
		ArrayList<BigInteger> coeficientesMonomio = new ArrayList<>();
		
		// Rellenamos con ceros hasta el grado que queremos conseguir
		for (int i = 0; i < gradoMonomio; i++) {
			coeficientesMonomio.add(BigInteger.ZERO);
		}
		
		// En el grado que se busca, establecemos el coeficiente que queremos
		coeficientesMonomio.add(coeficienteMonomio);
		
		return new Polinomio(modulo, coeficientesMonomio);
	}
	
	// Funcion para cambiar los signos de todos los coeficientes de un polinomio
	public static Polinomio negar(Polinomio polinomio) {
		ArrayList<BigInteger> nuevosCoeficientes = new ArrayList<BigInteger>();
		
		for (int i = 0; i < polinomio.getCoeficientes().size(); i++) {
			nuevosCoeficientes.add(polinomio.coeficientes.get(i).negate().mod(polinomio.getModulo()));
		}
		
		return new Polinomio(polinomio.getModulo(), nuevosCoeficientes);
	}
	
	// Modulo de un polinomio, es decir, resto de la division entre el polinomio de division correspondiente
	public static Polinomio modPolinomio(Polinomio polinomio, Polinomio polinomioDiv) {
		return Polinomio.dividir(polinomio, polinomioDiv)[1];
	}
	
	
	// OPERACIONES CON POLINOMIOS
	
	// Sumar polinomios
	public static Polinomio sumar(Polinomio a, Polinomio b) {

		int numCoeficientes = Math.max(a.coeficientes.size(), b.coeficientes.size());
		
		ArrayList<BigInteger> nuevosCoeficientes = new ArrayList<BigInteger>();
		
		// Vamos sumando los coeficientes de ambos polinomios, siempre que tengan
		for (int i = 0; i < numCoeficientes; i++) {
			
			BigInteger aCoeficiente = BigInteger.ZERO;
			BigInteger bCoeficiente = BigInteger.ZERO;
			
			if (i < a.coeficientes.size()) {
				aCoeficiente = a.coeficientes.get(i);
			}
			
			if (i < b.coeficientes.size()) {
				bCoeficiente = b.coeficientes.get(i);
			}
			
			nuevosCoeficientes.add(AritmeticaModular.sumar(aCoeficiente, bCoeficiente, a.modulo));
		}
		
		Polinomio resultado = new Polinomio(a.modulo, nuevosCoeficientes);
		resultado.limpiarPolinomio();
		return resultado;
	}
	
	
	// Restar polinomios
	public static Polinomio restar(Polinomio a, Polinomio b) {
		
		int numCoeficientes = Math.max(a.coeficientes.size(), b.coeficientes.size());
		
		ArrayList<BigInteger> nuevosCoeficientes = new ArrayList<BigInteger>();
		
		// Vamos restando los coeficientes de ambos polinomios, siempre que tengan
		for (int i = 0; i < numCoeficientes; i++) {
			
			BigInteger aCoeficiente = BigInteger.ZERO;
			BigInteger bCoeficiente = BigInteger.ZERO;
			
			if (i < a.coeficientes.size()) {
				aCoeficiente = a.coeficientes.get(i);
			} else {
				aCoeficiente = BigInteger.ZERO;
			}
			
			if (i < b.coeficientes.size()) {
				bCoeficiente = b.coeficientes.get(i);
			} else {
				bCoeficiente = BigInteger.ZERO;
			}
			
			nuevosCoeficientes.add(AritmeticaModular.restar(aCoeficiente, bCoeficiente, a.modulo));
		}
		
		Polinomio resultado = new Polinomio(a.modulo, nuevosCoeficientes);
		resultado.limpiarPolinomio();
		return resultado;
	}
	
	
	// Multiplicar polinomios
	public static Polinomio multiplicarOriginal(Polinomio a, Polinomio b) {
		
		int tamaño = a.coeficientes.size() + b.coeficientes.size() - 1;
		ArrayList<BigInteger> nuevosCoeficientes = new ArrayList<BigInteger>(tamaño);
		
		// Empezamos con todo ceros
		for (int k = 0; k < tamaño; k++) {
			nuevosCoeficientes.add(BigInteger.ZERO);
		}
				
		// Recorremos todos los terminos de los polinomios a y b
		for (int i = 0; i < a.coeficientes.size(); i++) {
			
			BigInteger aCoeficiente = a.coeficientes.get(i);
			
			if (aCoeficiente.equals(BigInteger.ZERO)) {
				continue;
			}
							
			for (int j = 0; j < b.coeficientes.size(); j++) {
				
				BigInteger bCoeficiente = b.coeficientes.get(j);
				
				if (bCoeficiente.equals(BigInteger.ZERO)) {
					continue;
				}
				
				// A lo que hubiera en la posicion, le añadimos el nuevo valor del producto de coeficientes
				BigInteger acumulado = nuevosCoeficientes.get(i + j);
				BigInteger añadido = AritmeticaModular.multiplicar(aCoeficiente, bCoeficiente, a.modulo);
				BigInteger nuevoValor = AritmeticaModular.sumar(acumulado, añadido, a.modulo);
				
				nuevosCoeficientes.set(i + j, nuevoValor);
			}
		}
		
		Polinomio resultado = new Polinomio(a.modulo, nuevosCoeficientes);
		resultado.limpiarPolinomio();
		return resultado;
	}
	
	// Funcion necesaria para aplicar metodo Karatsuba -> devuelve parte de un polinomio
	private static Polinomio partePolinomio(Polinomio polinomio, int comienzo, int fin) {
		ArrayList<BigInteger> coeficientes = polinomio.getCoeficientes();
		ArrayList<BigInteger> coeficientesParte = new ArrayList<BigInteger>();
		
		for (int i = comienzo; i < fin && i < coeficientes.size(); i++) {
			coeficientesParte.add(coeficientes.get(i));
		}
		
		// Si no se ha añadido nada, devolvemos el polinomio cero
		if (coeficientesParte.isEmpty()) {
			coeficientesParte.add(BigInteger.ZERO);
		}
		
		Polinomio partePolinomio = new Polinomio(polinomio.getModulo(), coeficientesParte);
		partePolinomio.limpiarPolinomio();
		return partePolinomio;
	}
	
	// Funcion necesaria para aplicar metodo Karatsuba -> multiplica polinomio por x^k
	// Consiste en desplazar los coeficientes k posiciones y rellenar el resto con ceros
	private static Polinomio multiplicarMonomio(Polinomio polinomio, int k) {
		if (polinomio.esCero() || k <= 0) {
			return polinomio;
		}
		
		ArrayList<BigInteger> nuevosCoeficientes = new ArrayList<BigInteger>();
		
		// Rellenamos las primeras k posiciones con ceros
		for (int i = 0; i < k; i++) {
			nuevosCoeficientes.add(BigInteger.ZERO);
		}
		
		// Añadimos los coeficientes originales despues de los ceros
		nuevosCoeficientes.addAll(polinomio.getCoeficientes());
		
		Polinomio nuevoPolinomio = new Polinomio(polinomio.getModulo(), nuevosCoeficientes);
		nuevoPolinomio.limpiarPolinomio();
		return nuevoPolinomio; 
	}
	
	// Multiplicacion Karatsuba
	public static Polinomio multiplicar(Polinomio a, Polinomio b) {
		// El tamaño corresponde con el del polinomio que tiene mas coeficientes
		int n = Math.max(a.getCoeficientes().size(), b.getCoeficientes().size());
		
		// Si los polinomios tienen menos de 64 terminos, se usa el metodo original
		if (n < 64) {
			return multiplicarOriginal(a, b);
		}
		
		int m = (n + 1) / 2;
		
		// Dividimos polinomio a en dos partes
		Polinomio a1 = partePolinomio(a, 0, m);
		Polinomio a2 = partePolinomio(a, m, a.getCoeficientes().size());
		
		// Dividimos polinomio b en dos partes
		Polinomio b1 = partePolinomio(b, 0, m);
		Polinomio b2 = partePolinomio(b, m, b.getCoeficientes().size());
		
		// Llamadas recursivas
		// c1 = a1 * b1
		Polinomio c1 = multiplicar(a1, b1);
		
		// c2 = a2 * b2
		Polinomio c2 = multiplicar(a2, b2);
		
		// c3 = (a1 + a2) * (b1 + b2)
		Polinomio c3 = multiplicar(sumar(a1, a2), sumar(b1, b2));
		
		// c3 = c3 - c1 - c2
		c3 = restar(restar(c3, c1), c2);
		
		// Resconstruccion: c2 * x^(2m) + c3 * x^m + c1
		Polinomio c2Nuevo = multiplicarMonomio(c2, 2 * m);
		Polinomio c3Nuevo = multiplicarMonomio(c3, m);
		
		Polinomio resultado = sumar(sumar(c2Nuevo, c3Nuevo), c1);
		resultado.limpiarPolinomio();
		return resultado;
	}
	

	// Dividir polinomios
	public static Polinomio[] dividir(Polinomio dividendo, Polinomio divisor) {
		
		// Comprobamos que el divisior no es cero
		if (divisor.esCero()) {
			throw new ArithmeticException("Division entre polinomio cero");
		}
		
		int n = dividendo.gradoPolinomio();
		int m = divisor.gradoPolinomio();

		// Si el dividendo es menor que el divisor, el cociente es 0 y el resto es el dividendo
		if (n < m) {
			Polinomio cocienteCero = crearMonomio(dividendo.getModulo(), 0, BigInteger.ZERO);
			Polinomio restoIgual = new Polinomio(dividendo.getModulo(), dividendo.getCoeficientes());
			return new Polinomio[] { cocienteCero, restoIgual };
		}
		
		BigInteger modulo = dividendo.getModulo();
		
		// Usamos arrays en vez de ArrayList
		ArrayList<BigInteger> aCoeficientess = dividendo.getCoeficientes();
		
		BigInteger[] resto = new BigInteger[n + 1];
		for (int i = 0; i <= n; i++) {
			resto[i] = aCoeficientess.get(i);
		}
		
		BigInteger[] cociente = new BigInteger[n - m + 1];
		for (int i = 0; i < cociente.length; i++) {
			cociente[i] = BigInteger.ZERO;
		}

		// Inverso del coeficiente mayor del divisor
		ArrayList<BigInteger> bCoeficientes = divisor.getCoeficientes();
		BigInteger inversoCoefDivisor = AritmeticaModular.inverso(bCoeficientes.get(m), modulo);
		
		for (int i = n; i >= m; i--) {
			if (resto[i].equals(BigInteger.ZERO)) {
				continue;
			}
			
			// Coeficiente del monomio correspondiente del cociente: resto[i] / divisor[i]
			BigInteger cocienteCoeficiente = AritmeticaModular.multiplicar(resto[i], inversoCoefDivisor, modulo);
			cociente[i - m] = cocienteCoeficiente;
			
			// Dividendo = divisor * cociente + resto -> resto = dividendo - divisor * cociente
			for (int j = 0; j <= m; j++) {
				BigInteger bCoeficiente = bCoeficientes.get(j);
				if (bCoeficiente.equals(BigInteger.ZERO)) {
					continue;
				}
				
				// resto[i - m + j] = resto[i - m + j] - (cocienteCoeficiente * bCoeficiente)
				BigInteger sustraendo = AritmeticaModular.multiplicar(cocienteCoeficiente, bCoeficiente, modulo);
				resto[i - m + j] = AritmeticaModular.restar(resto[i - m + j], sustraendo, modulo);
			}
		}
		
		// Con los arrays, generamos los polinomios
		ArrayList<BigInteger> cocienteCoeficientes = new ArrayList<>(cociente.length);
		for (BigInteger c : cociente) {
			cocienteCoeficientes.add(c);
		}
		Polinomio cocientePolinomio = new Polinomio(modulo, cocienteCoeficientes);
		cocientePolinomio.limpiarPolinomio();
		
		
		ArrayList<BigInteger> restoCoeficientes = new ArrayList<>(m);
		for (int i = 0; i < m; i++) {
			restoCoeficientes.add(resto[i]);
		}
		if (restoCoeficientes.isEmpty()) {
			restoCoeficientes.add(BigInteger.ZERO);
		}
		Polinomio restoPolinomio = new Polinomio(modulo, restoCoeficientes);
		restoPolinomio.limpiarPolinomio();

		return new Polinomio[] { cocientePolinomio, restoPolinomio };
	}
	
	
	// Algoritmo eucladiano para calcular el maximo comun denominador de dos polinomios
	public static Polinomio mcd(Polinomio a, Polinomio b) {
		a.limpiarPolinomio();
		b.limpiarPolinomio();
		
		Polinomio temp;
		
		while (!b.esCero()) {
			temp = b;
			b = dividir(a, b)[1];   // cogemos el resto
			a = temp;
			b.limpiarPolinomio();
		}
		
		return a;
	}
	
	
	// Exponenciar polinomio
	public static Polinomio exponenciar(Polinomio base, BigInteger exponente, Polinomio polinomioDiv) {
		Polinomio resultado = crearMonomio(base.modulo, 0, BigInteger.ONE);
		
		Polinomio baseActual = Polinomio.dividir(base, polinomioDiv)[1];
		BigInteger exp = exponente;
		
		// Mientras el exponente sea mayor de 0
		while (exp.compareTo(BigInteger.ZERO) > 0) {
		
			if (exp.testBit(0)) {
				resultado = multiplicar(baseActual, resultado);
				resultado = Polinomio.dividir(resultado, polinomioDiv)[1];   // modulo con el polinomio de division
			}
			
			exp = exp.shiftRight(1);   // desplaza todos los bits a la derecha una posicion
			
			if (exp.compareTo(BigInteger.ZERO) > 0) {
				baseActual = multiplicar(baseActual, baseActual);
				baseActual = Polinomio.dividir(baseActual, polinomioDiv)[1];
			}	
		}
		
		return resultado;
	}
}