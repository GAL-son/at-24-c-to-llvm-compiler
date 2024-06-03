int func() {
    int a = 12;
    return a;
}

void func2() {

}

int main() {

    int a = func();

    while(a < 10) {
        a = a + 2;
    }

    return a;
}