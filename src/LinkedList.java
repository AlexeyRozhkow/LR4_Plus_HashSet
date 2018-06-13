class LinkedList {
    private Wrapper first;
    private Wrapper firstQuarter;
    private Wrapper middle;
    private Wrapper lastQuarter;
    private Wrapper last;
    private int size = 0;

    LinkedList() {
    }

    Object add(Object object) {
        size++;
        Wrapper tmp = last;
        Wrapper newObj = new Wrapper(tmp, object, null);
        last = newObj;
        if (tmp == null) {
            first = newObj;
            firstQuarter = newObj;
            middle = newObj;
            lastQuarter = newObj;
        } else {
            tmp.setNext(newObj);
            if ((size - 1) % 2 == 1) {
                middle = middle.getNext();
            }
            if ((size - 1) % 4 == 1) {
                firstQuarter = firstQuarter.getNext();
            }
            if ((size - 1) % 4 != 0) {
                lastQuarter = lastQuarter.getNext();
            }
        }
        return object;
    }

    boolean remove(Object object) {
        int index = 0;
        Wrapper temp = first;

        while (index < size) {
            if (temp.getCurrent().equals(object)) {
                Wrapper previous = temp.getPrevious();
                Wrapper next = temp.getNext();

                editLinks(index);
                if (previous == null) {
                    first = next;
                } else {
                    previous.setNext(next);
                    temp.setPrevious(null);
                }
                if (next == null) {
                    last = previous;
                } else {
                    next.setPrevious(previous);
                    temp.setNext(null);
                }
                temp.setCurrent(null);
                size--;
                return true;
            }
            index++;
            temp = temp.getNext();
        }
        return false;
    }

    int indexOf(Object object) {
        int index = 0;
        Wrapper temp = first;

        while (index < size) {
            if (temp.getCurrent().equals(object)) {
                return index;
            }
            index++;
            temp = temp.getNext();
        }
        return -1;
    }

    Object get(int index) {
        if (index < size) {
            int[] dir = getDirection(index);
            Wrapper start = getWrapper(dir[0]);
            int startIndex = dir[1];

            switch (dir[2]) {
                case 0:
                    break;
                case -1:
                    while (startIndex < index) {
                        startIndex++;
                        try {
                            start = start.getNext();
                        } catch (NullPointerException e) {
                            System.err.println("Error: NullPointerException");
                            System.exit(105);
                        }
                    }
                    break;
                case 1:
                    while (startIndex > index) {
                        startIndex--;
                        try {
                            start = start.getPrevious();
                        } catch (NullPointerException e) {
                            System.err.println("Error: NullPointerException");
                            System.exit(105);
                        }
                    }
                    break;
            }
            try {
                return start.getCurrent();
            } catch (NullPointerException e) {
                System.err.println("Error: NullPointerException");
                System.exit(105);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Wrapper temp = first;//

        sb.append("[");
        if (size != 0) {
            for (int i = 0; i < size - 1; i++) {
                sb.append(temp.getCurrent());//
                sb.append(", ");
                temp = temp.getNext();//
            }
            sb.append(temp.getCurrent());//
        }
        sb.append("]");
        return sb.toString();
    }

    boolean contains(Object obj) {
        return indexOf(obj) >= 0;
    }

    int size() {
        return size;
    }

    //Метод нормализует пропорционально расположение промежуточных ссылок (firstQuarter, middle, lastQuarter)
    private void editLinks(int index) {
        int[] indexes = {calc(.25), calc(.5), calc(.75)};

        if (indexes[0] < index) {
            if ((size - 1) % 4 == 1) {
                firstQuarter = firstQuarter.getPrevious();
            }
        } else if (indexes[0] == index) {
            if ((size - 1) % 4 == 1) {
                firstQuarter = firstQuarter.getPrevious();
            } else {
                firstQuarter = firstQuarter.getNext();
            }
        } else {
            if ((size - 1) % 4 != 1) {
                firstQuarter = firstQuarter.getNext();
            }
        }
        if (indexes[1] < index) {
            if ((size - 1) % 2 == 1) {
                middle = middle.getPrevious();
            }
        } else if (index == indexes[1]) {
            if ((size - 1) % 2 == 1) {
                middle = middle.getPrevious();
            } else {
                middle = middle.getNext();
            }
        } else {
            if ((size - 1) % 2 != 1) {
                middle = middle.getNext();
            }
        }
        if (indexes[2] < index) {
            if ((size - 1) % 4 != 0) {
                lastQuarter = lastQuarter.getPrevious();
            }
        } else if (index == indexes[2]) {
            if ((size - 1) % 4 != 0) {
                lastQuarter = lastQuarter.getPrevious();
            } else {
                lastQuarter = lastQuarter.getNext();
            }
        } else {
            if ((size - 1) % 4 == 0) {
                lastQuarter = lastQuarter.getNext();
            }
        }
    }

    private Wrapper getWrapper(int i) {
        switch (i) {
            case 0:
                return first;
            case 1:
                return firstQuarter;
            case 2:
                return middle;
            case 3:
                return lastQuarter;
            case 4:
                return last;
            default:
                System.err.println();
                System.exit(103);
        }
        return null;
    }

    //Метод позволяет определить индексы для каждого из ссылок (first, firstQuarter, middle, lastQuarter, last)
    private int[] getDirection(int index) {
        int[] direction = new int[3];
        int min = size;
        int[] indexes = {0, calc(.25), calc(.5), calc(.75), size - 1};

        for (int i = 0; i < indexes.length; i++) {
            int tmp = Math.abs(indexes[i] - index);
            if (tmp < min) {
                min = tmp;
                direction[0] = i;
                direction[1] = indexes[i];
                try {
                    direction[2] = (indexes[i] - index) / tmp;
                } catch (ArithmeticException e) {
                    direction[2] = 0;
                    break;
                }
            }
        }
        return direction;
    }

    private int calc(double d) {
        return (int) Math.ceil(sizeDoubleDec() * d);
    }

    private double sizeDoubleDec() {
        return (double) size - 1;
    }
}