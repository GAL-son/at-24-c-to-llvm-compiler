@TEN = global i32 10
define i32 @fibo ( i32 %limit ) {
	%a = alloca i32
	store i32 0, i32* %a
	%b = alloca i32
	store i32 1, i32* %b
	%counter = alloca i32
	store i32 0, i32* %counter
	br label %label.1.whileReset
	label.1.whileReset:
	%1 = load i32, i32* %counter
	%2 = icmp slt i32 %1, %limit
	br i1 %2, label %label.1.whileStart, label %label.1.whileExit
	label.1.whileStart:
		%sum = alloca i32
		%3 = load i32, i32* %a
		%4 = load i32, i32* %b
		%5 = add i32 %3, %4
		store i32 %5, i32* %sum
	br label %label.1.whileReset
	label.1.whileExit:
	%6 = load i32, i32* %a
	ret i32 %6
}

define i32 @main () {
	%x = alloca i32
	%1 = load i32, i32* @TEN
	%2 = call i32 @fibo (i32 %1)
	store i32 %2, i32* %x
	%3 = load i32, i32* %x
	ret i32 %3
}

