## Types

### Integers

Due to LLVM IR not handling signed/unsigned values, every integer will be assinned to memory location of twice the size to make it easier to implement

| C type | Chonsen LLVM IR type | Value range | 
|:------:|:------------:|:-|
|`char`|`i2`| -127 to 128
|`short` |`i4`| Two byte integer|
|`int`|`i4`| Four Byte integer|
|`long`|`i4`| Also Four bytes|
