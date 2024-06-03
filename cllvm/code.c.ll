define i32 @f () {
	ret i32 1
}

define i32 @main () {
	%a = alloca i32
	store i32 1, i32* %a
	ret i32 1
}

