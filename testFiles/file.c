int TEN = 10;

int fibo(int limit) {
    int a = 0;
    int b = 1;
    
    int counter = 0;

    while (counter < limit)
    {
        int sum = a+b;
        a = b;
        b = sum;

        counter++;
    }

    return a;
    
}

int main() {
    int x = fibo(TEN);

    return x;
}