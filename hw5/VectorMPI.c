#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>




int main( argc, argv )
int argc;
char **argv;
{
int rank, size;
MPI_Init( &argc, &argv );
MPI_Comm_rank( MPI_COMM_WORLD, &rank );
MPI_Comm_size( MPI_COMM_WORLD, &size );
int **RowArray;
if(rank==0){
	int numCol = 0;
	int Vector[100];
	FILE *VectorFile;
	VectorFile = fopen("vector.txt", "r");
	char buff[4096];
	char *buffPtr;
	int Vidx = 0;
	fgets(buff, sizeof buff, VectorFile);
	buffPtr = buff;
	while(1){
		if(*buffPtr == '\n') break;
		Vector[Vidx] = strtol(buffPtr, &buffPtr, 10);
		
		Vidx++;
	}
	numCol = Vidx;

	int Matrix[1000];
	int numRow =0;
	FILE *MatrixFile;
	MatrixFile = fopen("matrix.txt", "r");	
	int Midx = 0;
	fgets(buff, sizeof buff, MatrixFile);
	buffPtr = buff;
	numRow = strtol(buffPtr, &buffPtr, 10);
	int trash = strtol(buffPtr, &buffPtr, 10);
	while(1){
		if(!fgets(buff, sizeof buff, MatrixFile)) break;
		buffPtr = buff;
		while(1){
			if(*buffPtr == '\n') break;
			Matrix[Midx] = strtol(buffPtr, &buffPtr, 10);
			Midx++;	
		}
	}
	//	int **ColArray;
        const size_t row_pointers_bytes = numRow * sizeof *RowArray;
        const size_t row_elements_bytes = numCol * sizeof **RowArray;
        RowArray = malloc(row_pointers_bytes + numRow * row_elements_bytes);
        size_t i;
        int * const data = RowArray + numRow;
        for(i = 0; i < numRow; i++){
                RowArray[i] = data + i * numCol;
        }
//	printf("numcol%d numrow%d midx%d\n", numCol, numRow, Midx);	

	int MAidx = 0;
	for(int i =0;i<numRow;i++){
		for(int j =0;j<numCol;j++){
			RowArray[i][j] = Matrix[MAidx];
			MAidx++;
		}
	}

	fclose(VectorFile);
	fclose(MatrixFile);
	if(size <= 1){
		//just do all of it myself
	
		int orders[100];
		for(int i= 0; i<numRow;i++){	
			int sum = 0;
        		for(int j =0;j<numCol;j++){
				int tempa = Vector[j];
				int tempb = RowArray[i][j];
                		sum+= (tempb)*(tempa);
        		}
			orders[i] = sum;
		}	
			
		FILE *resultFile;
		resultFile=fopen("result.txt", "w");
		for(int i =0;i<numRow;i++){
			fprintf(resultFile, "%d ", orders[i]);
		}
		fclose(resultFile);
	}else{
		//split work amongst every one, if it doesn't divide out i'll take the rest
		int myWork = numRow % (size-1);//myWork=number of rows i (root) must do
		int othersWork = numRow - myWork;
		othersWork = othersWork/(size-1);//othersWork=number of rows other processes must do
		//send out the requests
		int rowIdx = myWork;
		for(int i = 1; i<size; i++){//for each process other than me
			MPI_Send(&numCol, 1, MPI_INT, i, 0, MPI_COMM_WORLD); 	//numCol
			MPI_Send(&numRow, 1, MPI_INT, i, 0, MPI_COMM_WORLD); 	//numRow
			MPI_Send(&othersWork, 1, MPI_INT, i, 0, MPI_COMM_WORLD); 	//send amount of work
			MPI_Send(&Vector, numCol, MPI_INT, i, 0, MPI_COMM_WORLD); //theVector
			for(int j=rowIdx;j<(rowIdx+othersWork);j++){//take two
				int idd = j;
				 MPI_Send(&idd, 1, MPI_INT, i, 0, MPI_COMM_WORLD); 				
				MPI_Send(RowArray[j], numCol, MPI_INT, i, 0, MPI_COMM_WORLD);
			}
			rowIdx += othersWork;
		}
		//i do my work
		int orders[1000];
		for(int i= 0; i<myWork;i++){	
			int sum = 0;
        		for(int j =0;j<numCol;j++){
                		sum+= (RowArray[i][j])*(Vector[j]);
        		}
			orders[i] = sum;
		}		
		//barriers
		MPI_Barrier(MPI_COMM_WORLD);
		//recieveresults
		for(int i = 1;i<size;i++){
			int Temp[1000];
			MPI_Recv(&Temp,numRow, MPI_INT,i,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
			for(int j = 0; j<numRow; j++){
				orders[j]+=Temp[j];
			}
		}
		MPI_Barrier(MPI_COMM_WORLD);
		//write to file
		FILE *resultFile;
		resultFile=fopen("result.txt", "w");
		for(int i =0;i<numRow;i++){
			fprintf(resultFile, "%d ", orders[i]);
		//	fputc(' ' , resultFile );
		}
		fclose(resultFile);
		
	}
	
//	MPI_Send(&numCol, 1, MPI_INT, 1, 0, MPI_COMM_WORLD); 	
//	MPI_Send(&numRow, 1, MPI_INT, 1, 0, MPI_COMM_WORLD); 	
//	MPI_Send(ColArray[1], 7, MPI_INT,1, 0, MPI_COMM_WORLD);	

}else{
	int numCol=-1;
	int numRow=-1;
	int myWork=-1;
	int Vector[1000];
	int Row[1000];
	int orders[1000];	
	MPI_Recv(&numCol,1 , MPI_INT, 0,0,MPI_COMM_WORLD, MPI_STATUS_IGNORE);
	MPI_Recv(&numRow,1 , MPI_INT, 0,0,MPI_COMM_WORLD, MPI_STATUS_IGNORE);
	MPI_Recv(&myWork,1 , MPI_INT, 0,0,MPI_COMM_WORLD, MPI_STATUS_IGNORE);
	//Vector = malloc(sizeof(int)*numRow);
	//Column = malloc(sizeof(int)*numRow);
	MPI_Recv(&Vector,numCol, MPI_INT,0,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
	for(int i=0;i<myWork;i++){
		int order = -1;
		MPI_Recv(&order,1 , MPI_INT, 0,0,MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		MPI_Recv(&Row, numCol, MPI_INT, 0,0,MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		/*printf("\nPROCESS %d GIVEN ROW ", rank);
		for(int i = 0;i<numCol;i++){
			printf("%d ",Row[i]);
		}*/

		int sum = 0;
        	for(int i =0;i<numCol;i++){
                	sum+= (Row[i])*(Vector[i]);
        	}
		orders[order] = sum;
	//	printf("\nPROCESS %d HANDLE COLUMN %d AND GOT %d",rank,order,orders[order]);
	}
	//wait for everyone else
	MPI_Barrier(MPI_COMM_WORLD);
	//send back Vector
	MPI_Send(&orders, numRow, MPI_INT, 0, 0, MPI_COMM_WORLD);
	MPI_Barrier(MPI_COMM_WORLD);

}


	
MPI_Finalize();
return 0;
}

