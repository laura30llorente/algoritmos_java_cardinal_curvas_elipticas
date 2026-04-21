package com.utad.mais.tfgmaco.operaciones;

import java.math.BigInteger;

import com.utad.mais.tfgmaco.curva.CurvaElipticaFp;
import com.utad.mais.tfgmaco.polinomios.OperacionesPuntoPolinomico;
import com.utad.mais.tfgmaco.polinomios.Polinomio;
import com.utad.mais.tfgmaco.polinomios.PuntoProyectivoPolinomico;
import com.utad.mais.tfgmaco.punto.PuntoFp;

// Para multiplicar un punto por un escalar de forma eficiente
public class DoubleAndAdd {
	
	// CASO AFIN
	public static PuntoFp algoritmoDoubleAddAfin(PuntoFp P, BigInteger escalar) {
		// Empezamos en el punto en el infinito
		PuntoFp output = new PuntoFp(P.getCurva());
		
		// Considerando el escalar en binario
		int l = escalar.bitLength();
		
		// Double-and-add de izquierda a derecha
		for (int i = l - 1; i >= 0; i--) {
			// Duplicamos el output
			output = SumaPuntos.sumarPuntos(output, output);
			
			// Comprobamos si k_i == 1
			// testBit devuelve true si el bit a comprobar esta activado
			if (escalar.testBit(i)) {
				output = SumaPuntos.sumarPuntos(output, P);
			}
		}
		
		return output;
	}
	
	// CASO PROYECTIVO
	public static PuntoProyectivoPolinomico algoritmoDoubleAddProyectivo(
			BigInteger escalar, PuntoProyectivoPolinomico P, 
			Polinomio polinomioDiv, CurvaElipticaFp curva, Polinomio curvaPolinomio) {
	    
		PuntoProyectivoPolinomico resultado = new PuntoProyectivoPolinomico(curva.getModulo(), curvaPolinomio, polinomioDiv);

		int l = escalar.bitLength();
		
		// En vez de multiplicar el punto k veces, usamos la idea de que el escalar se puede escribir como potencia
	    for (int i = l - 1; i >= 0; i--) {
	    	// Duplicamos el resultado
            resultado = OperacionesPuntoPolinomico.sumarPuntosPolinomicosProyectivos(resultado, resultado, polinomioDiv, curva, curvaPolinomio);

            // Comprobamos si k_i == 1
	        if (escalar.testBit(i)) {
	            resultado = OperacionesPuntoPolinomico.sumarPuntosPolinomicosProyectivos(resultado, P, polinomioDiv, curva, curvaPolinomio);
	        }
	        
	    }
	    
	    return resultado;
	}
}