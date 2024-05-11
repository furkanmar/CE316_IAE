#include <stdio.h>
//#include <limits.h>

int main(){
	// you can also use the two values below by including <limits.h> library up there
	// int largest1 = INT_MIN, largest2 = INT_MIN;
	int largest1 = -100000, largest2 = -100000;
	int value=67;
	int count;

	for(count=0; count<10; count++){
		printf("\n Please enter a number: ");

		if(value > largest1){
			largest2 = largest1;
			largest1 = value;
		}
		else if(value != largest1 && value > largest2){
			largest2 = value;
		}
	}
	printf("\n The largest two numbers are %d and %d", largest1, largest2);
}
