int func(int b) {
    int a = 12;
    return a;
}

void func2() {

}

int main() {
    func(12);
    int a = func();

    while(a < 10) {
        a = a + 2;
    }

    return a;
}