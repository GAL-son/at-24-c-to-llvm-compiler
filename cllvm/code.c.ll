define i32 @func () {
	%a = alloca i32
	store i32 12, i32* %a
	%1 = load i32, i32* %a
	ret i32 %1
}

define void @func2 () {
ret void
}

define i32 @main () {
	%a = alloca i32
	%1 = call i32 @func ()
	store i32 %1, i32* %a
	br label %label.1.whileReset
	label.1.whileReset:
	%2 = load i32, i32* %a
	%3 = icmp slt i32 %2, 10
	br i1 %3, label %label.1.whileStart, label %label.1.whileExit
	label.1.whileStart:
	br label %label.1.whileReset
	label.1.whileExit:
	%4 = load i32, i32* %a
	ret i32 %4
}

