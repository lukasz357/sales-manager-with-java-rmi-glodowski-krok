/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package salesmanager.FileParsers;

/**
 *
 * @author Łukasz
 */
public class ArgsParser {

    public int parseArgs(String[] args) throws BadArgsException {
        switch (args.length) {
            case 0:
                throw new BadArgsException("Nie podano żadnych argumentów");
            case 1:
                 if (args[0].compareTo("-clearBase") == 0) {
                    return CLEAR_BASE;
                } else if (args[0].compareTo("-makeOrders") == 0) {
                    return MAKE_ORDERS;
                } else {
                    throw new BadArgsException("Nie ma takiego argumentu");
                }
            case 2:
                if (args[0].compareTo("-delP") == 0) {
                    return DEL_PRODUCT;
                } else if (args[0].compareTo("-delD") == 0) {
                    return DEL_DRIVER;
                } else if (args[0].compareTo("-addOrders") == 0) {
                    return ADD_ORD_FROM_FILE;
                } else if (args[0].compareTo("-addMap") == 0) {
                    return ADD_MAP;
                } else {
                    throw new BadArgsException("Nie ma takiego argumentu");
                }
            case 3:
                if (args[0].compareTo("-addP") == 0) {
                    if(args[1].compareTo("-f") == 0) {
                        return ADD_PR_FROM_FILE;
                    }
                    else {
                        System.out.println("Nieprawidłowy drugi argument.");
                        System.exit(1);
                    }
                    return ADD_PR_FROM_FILE;
                }
                else if(args[0].compareTo("-addD") == 0) {
                    return ADD_DRIVER;
                } else {
                    throw new BadArgsException("Nie ma takiego argumentu");
                }
            case 4:
                if (args[0].compareTo("-addP") == 0) {
                    return ADD_PRODUCT;
                } else {
                    throw new BadArgsException("Nie ma takiego argumentu");
                }
            default:
                throw new BadArgsException("Nieprawidłowa liczba argumentów");
        }
    }

    public static class BadArgsException extends Exception {

        public BadArgsException() {
        }

        public BadArgsException(String gripe) {
            super(gripe);
        }
    }
    private static final int CLEAR_BASE = 1;
    private static final int MAKE_ORDERS = 2;
    private static final int DEL_PRODUCT = 3;
    private static final int DEL_DRIVER = 4;
    private static final int ADD_PR_FROM_FILE = 5;
    private static final int ADD_ORD_FROM_FILE = 6;
    private static final int ADD_PRODUCT = 7;
    private static final int ADD_DRIVER = 8;
    private static final int ADD_MAP = 9;
}
