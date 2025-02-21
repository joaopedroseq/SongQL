package aor.paj;

import java.util.GregorianCalendar;

public abstract class ValidacaoInput {

    /**
     * Valida se a string fornecida está em conformidade com critérios específicos. A string
     * não deve estar vazia e só pode conter letras e espaços.
     *
     * @param string a string a validar
     * @return true se a string contiver apenas letras e espaços e não estiver vazia,
     * falso caso contrário
     */
    public static boolean validar(String string) {
        string  = string.trim();
        if (string.isEmpty()) {
            return false;
        } else {
            for (int i = 0; i < string.length(); i++) {
                if (!Character.isLetter(string.charAt(i))) {
                    if (!Character.isSpaceChar(string.charAt(i))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Valida se a string fornecida é um número inteiro válido.
     * A string não deve estar vazia, deve conter apenas dígitos numéricos,
     * e deve ter no máximo 9 caracteres.
     *
     * @param valorInteger a string a validar como um número inteiro
     * @return true se a string representa um número inteiro válido, false caso contrário
     */
    public static boolean validarInt(String valorInteger) {
        valorInteger = valorInteger.trim();
        if (valorInteger.isEmpty()) {
            return false;
        } else if (valorInteger.length() > 9) {
            return false;
        } else {
            for (int i = 0; i < valorInteger.length(); i++) {
                if (!Character.isDigit(valorInteger.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Valida se a string fornecida representa um valor duplo válido.
     * A string não deve estar vazia, pode conter um ponto decimal e todos os outros caracteres devem ser dígitos numéricos.
     *
     * @param valorDouble a string a validar como double
     * @return true se a string representa um double válido, false caso contrário
     */
    public static boolean validarDouble(String valorDouble) {
        valorDouble = valorDouble.trim();
        int numPontos = 0;
        if (valorDouble.isEmpty()) {
            return false;
        } else {
            for (int i = 0; i < valorDouble.length(); i++) {
                if (valorDouble.charAt(i) == '.') {
                    numPontos++;
                } else if (!Character.isDigit(valorDouble.charAt(i))) {
                    return false;
                }
                if (numPontos > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Valida a string fornecida de acordo com critérios específicos.
     * A string não pode estar vazia e não pode conter espaços, dependendo do valor do parâmetro ePalavraChave.
     *
     * @param string a string a ser validada
     * @param ePalavraChave um booleano indicando critérios específicos; true se a validação for para uma palavra-chave
     * @return true se a string for válida de acordo com os critérios; false caso contrário
     */
    public static boolean validar(String string, Boolean ePalavraChave) {
        if (string.isEmpty()) {
            return false;
        } else {
            for (int i = 0; i < string.length(); i++) {
                if ((string.charAt(i) == ' ')) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Valida se uma determinada string representa um número inteiro válido dentro de um intervalo especificado.
     * A string não deve estar vazia, deve conter apenas caracteres numéricos,
     * e a sua representação inteira devem estar entre os valores mínimo e máximo especificados.
     *
     * @param string a string a validar
     * @param min o valor mínimo permitido para o número inteiro
     * @param max o valor máximo permitido para o número inteiro
     * @return true se a string representa um número inteiro válido dentro do intervalo, false caso contrário
     */
    public static boolean validar(String string, int min, int max) {
        string = string.trim();
        if (string.isEmpty()) {
            return false;
        } else {
            for (int i = 0; i < string.length(); i++) {
                if (i == 0 && string.charAt(i) == '-') {
                } else if (!Character.isDigit(string.charAt(i))) {
                    return false;
                }
            }
        }
        int user_input = Integer.parseInt(string);
        if (user_input < min || user_input > max) {
            return false;
        }
        return true;
    }

    /**
     * Valida se uma determinada string representa um número inteiro válido maior ou igual a um valor mínimo especificado.
     * A string não deve estar vazia e deve ser constituída apenas por caracteres numéricos.
     *
     * @param string a string a ser validada como um número inteiro
     * @param min o valor mínimo inteiro permitido
     * @return true se a string representa um número inteiro maior ou igual ao valor mínimo, false caso contrário
     */
    public static boolean validar(String string, int min) {
        string = string.trim();
        if (string.isEmpty()) {
            return false;
        } else {
            for (int i = 0; i < string.length(); i++) {
                if (i == 0 && string.charAt(i) == '-') {
                } else if (!Character.isDigit(string.charAt(i))) {
                    return false;
                }
            }
        }
        int user_input = Integer.parseInt(string);
        if (user_input < min) {
            return false;
        }
        return true;
    }

    /**
     * Valida se a data final é posterior à data inicial fornecidas.
     *
     * @param dataInicio a data inicial a ser comparada
     * @param dataFim a data final a ser comparada
     * @return true se a data final for posterior à data inicial, false caso contrário
     */
    public static boolean validar(GregorianCalendar dataInicio, GregorianCalendar dataFim) {
        if (dataFim.compareTo(dataInicio) <= 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Remove caracteres especiais de uma string, substituindo letras acentuadas e outros caracteres especiais
     * com os seus equivalentes não acentuados.
     *
     * @param string a string de entrada que contém caracteres especiais
     * @return uma string com caracteres especiais substituídos pelos seus equivalentes não acentuados
     */
    public static String removerCarateresEspeciais(String string){
        String resultado = string.replaceAll("[\'\"]","");
        return resultado;
    }
    }