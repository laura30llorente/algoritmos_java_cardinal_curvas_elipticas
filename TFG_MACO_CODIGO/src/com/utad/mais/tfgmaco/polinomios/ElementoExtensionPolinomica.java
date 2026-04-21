package com.utad.mais.tfgmaco.polinomios;

import java.math.BigInteger;

public class ElementoExtensionPolinomica {
	// El elemento extension sera de la forma A(x) + B(x)y
	private Polinomio a;
	private Polinomio b;
	private Polinomio polinomioCurva;
	private Polinomio polinomioDiv;
	
	public ElementoExtensionPolinomica(Polinomio a, Polinomio b, Polinomio polinomioCurva, Polinomio polinomioDiv) {
		this.polinomioDiv = polinomioDiv;
		
		// Creamos el elemento de extension con todos los elementos (mod polinomio division), para que los posteriores calculos mantengan la congruencia
		this.a = Polinomio.modPolinomio(a, this.polinomioDiv);
		this.b = Polinomio.modPolinomio(b, this.polinomioDiv);
		this.polinomioCurva = Polinomio.modPolinomio(polinomioCurva, this.polinomioDiv);	
	}

	// Metodos getters y setters
	
	public Polinomio getA() {
		return a;
	}

	public void setA(Polinomio a) {
		this.a = a;
	}

	public Polinomio getB() {
		return b;
	}

	public void setB(Polinomio b) {
		this.b = b;
	}
	
	public Polinomio getPolinomioCurva() {
		return polinomioCurva;
	}

	public void setPolinomioCurva(Polinomio polinomioCurva) {
		this.polinomioCurva = polinomioCurva;
	}
	
	public Polinomio getPolinomioDiv() {
		return polinomioDiv;
	}

	public void setPolinomioDiv(Polinomio polinomioDiv) {
		this.polinomioDiv = polinomioDiv;
	}
	
	// Metodos utiles

	public static ElementoExtensionPolinomica elementoCero(BigInteger modulo, Polinomio polinomioCurva, Polinomio polinomioDiv) {
		Polinomio cero = Polinomio.crearMonomio(modulo, 0, BigInteger.ZERO);
		return new ElementoExtensionPolinomica(cero, cero, polinomioCurva, polinomioDiv);   // sera 0 + 0 * y = 0
	}
	
	public static ElementoExtensionPolinomica elementoUno(BigInteger modulo, Polinomio polinomioCurva, Polinomio polinomioDiv) {
		Polinomio cero = Polinomio.crearMonomio(modulo, 0, BigInteger.ZERO);
		Polinomio uno = Polinomio.crearMonomio(modulo, 0, BigInteger.ONE);
		return new ElementoExtensionPolinomica(uno, cero, polinomioCurva, polinomioDiv);    // sera 1 + 0 * y = 1
	}
	
	public boolean esCero() {
		return this.a.esCero() && this.b.esCero();    // Si ambas partes del elemento de extension son cero
	}
	
	
	// OPERACIONES
	
	// Suma:  (A_1(x) + B_1(x)y) + (A_2(x) + B_2(x)y) = (A_1(x) + A_2(x)) + (B_1(x) + B_2(x)) * y
	public ElementoExtensionPolinomica sumar(ElementoExtensionPolinomica polinomio) {
		Polinomio sumaA = Polinomio.sumar(this.getA(), polinomio.getA()); 
		Polinomio sumaB = Polinomio.sumar(this.getB(), polinomio.getB()); 
		
		return new ElementoExtensionPolinomica(sumaA, sumaB, this.polinomioCurva, this.polinomioDiv);
	}
	
	// Resta:  (A_1(x) + B_1(x)y) - (A_2(x) + B_2(x)y) = (A_1(x) - A_2(x)) + (B_1(x) - B_2(x)) * y
	public ElementoExtensionPolinomica restar(ElementoExtensionPolinomica polinomio) {
		Polinomio restaA = Polinomio.restar(this.getA(), polinomio.getA()); 
		Polinomio restaB = Polinomio.restar(this.getB(), polinomio.getB()); 
		
		return new ElementoExtensionPolinomica(restaA, restaB, this.polinomioCurva, this.polinomioDiv);
	}
	
	// Producto:  (A_1(x) + B_1(x)y) * (A_2(x) + B_2(x)y) = (A_1(x) * A_2(x) + B_1(x) * B_2(x) * polinomioCurva) + (A_1(x) * B_2(x) + B_1(x) * A_2(x)) * y
	public ElementoExtensionPolinomica multiplicar(ElementoExtensionPolinomica polinomio) {
		Polinomio producto1Termino1 = Polinomio.multiplicar(this.getA(), polinomio.getA());
		Polinomio producto2Termino1 = Polinomio.multiplicar(Polinomio.multiplicar(this.getB(), polinomio.getB()), this.polinomioCurva);
		Polinomio termino1 = Polinomio.sumar(producto1Termino1, producto2Termino1);
		
		Polinomio producto1Termino2 = Polinomio.multiplicar(this.getA(), polinomio.getB());
		Polinomio producto2Termino2 = Polinomio.multiplicar(this.getB(), polinomio.getA());
		Polinomio termino2 = Polinomio.sumar(producto1Termino2, producto2Termino2);
		
		return new ElementoExtensionPolinomica(termino1, termino2, this.polinomioCurva, this.polinomioDiv);
	}
	
	// Producto por una constante
	public ElementoExtensionPolinomica multiplicarConstante(BigInteger constante) {
		Polinomio polinomioConstante = Polinomio.crearMonomio(this.a.getModulo(), 0, constante);
		Polinomio productoA = Polinomio.multiplicar(this.getA(), polinomioConstante);
		Polinomio productoB = Polinomio.multiplicar(this.getB(), polinomioConstante);
		
		return new ElementoExtensionPolinomica(productoA, productoB, this.polinomioCurva, this.polinomioDiv);
	}
	
	// Exponenciar
	public ElementoExtensionPolinomica exponenciar(BigInteger exponente) {
		if (exponente.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("El exponente debe ser positivo");
		}
		
		BigInteger exp = exponente;
		ElementoExtensionPolinomica base = this;
		ElementoExtensionPolinomica resultado = ElementoExtensionPolinomica.elementoUno(this.a.getModulo(), this.polinomioCurva, this.polinomioDiv);
		
		while (exp.compareTo(BigInteger.ZERO) > 0) {
			// Si el primer bit es un 1, el numero es impar
			if (exp.testBit(0)) {
				resultado = resultado.multiplicar(base);
			}
			
			base = base.multiplicar(base);
			
			exp = exp.shiftRight(1);        // desplaza todos los bits a la derecha una posicion
		}
		
		return resultado;
	}
}
