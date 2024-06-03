define i32 @main () {
	%x = alloca i32
	store i32 0, i32* %x
	%1 = load i32, i32* %x
	ret i32 %1
	}
