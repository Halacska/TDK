#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>

#define d2r (M_PI/180.0)

typedef struct nod {
  float x;                   // x coordinate of the node
  float y;                   // y coordinate of the node
  char  NumSegm;             // Number of connected road segments at the given node
  unsigned short SgmList[7]; // List of IDs of connected segments
  } Node;

typedef struct sgm {
  unsigned short N1;  // One of the endpoints (node ID)
  unsigned short N2;  // Other endpoint (node ID)
  char type;          // Type of segment
  } Segment;

typedef struct sgm2{
  unsigned short  N1;       // One of the end of segmentchain (node ID)
  unsigned short  N2;       // Other endpoint of segmentchain (node ID)
  unsigned short  NumSegm;  // Number of segments along the segmentchain (beetween crossroads)
  unsigned short *SgmList;  // Address of a dynamicaly allocated array storing the IDs of segments between crossroads
  float           Length;   // Sum of length of segments (distance between crossroads)
  float           Duration;  // Time to be needed to go between endpoints
  } Segment2;

typedef struct nod2{
  char  NumChain;              // Number of connected segmentchains at the given crossroad (0 for skipped nodes with degree 2)
  unsigned short ChainList[7]; // List of IDs of connected segmentchains (index of SGM2)
  } Node2;

typedef struct pth{
  unsigned short  Start;     // ID of the node where the path is originated
  float           Duration;  // Duration of travel along the path in seconds (affected by speed) (needed only for statistics)
  float           Length;    // Length of the whole path in meters (needed only for statistics)
  unsigned short  NSegm;     // Number of road segments along the path
  unsigned short *SgmList;   // Address of a dynamicaly allocated array storing the IDs of segments in the path
  } Path;


//a szegmens hossza m-ben
float SGMlength(Node N1, Node N2){ //y latitude x longitude
	return (float) sqrt(pow((N1.x - N2.x),2) + pow((N1.y - N2.y),2));
}

float SGMduration(double length, char type){
	double speed;
	switch (type){
		case 0: speed = 100.0/3.6; break;
		case 1: speed = 30.0/3.6; break;
		case 2: speed = 28.0/3.6; break;
		case 3: speed = 26.0/3.6; break;
		case 4: speed = 24.0/3.6; break;
		case 5: speed = 22.0/3.6; break;
		case 6: speed = 18.0/3.6; break;
	}
	return length/speed;
}

int next(Path* arr, unsigned short Nn, char* used){
	float min = 100001;
	int index;	
	for (int i = 0; i < Nn; i++){
		if (arr[i].Duration == 0 || arr[i].Duration == -1 || used[i] == 1)			
			continue;
		if (min > arr[i].Duration){			
			min = arr[i].Duration;			
			index = i;		
		}	
	}
	if (min == 100001)
		index = -1;
	return index;
}

int main(int argc, char const *argv[]) {
	int Nn = 0, Ns = 0;
	FILE* fn = fopen("test_giant.nod", "r");
	FILE* fs = fopen("test_giant.sgm", "r");

	//node-ok számának meghatározása (=sorok száma)
	while(!feof(fn)) { 		
		if(fgetc(fn) == '\n')
			Nn++;
	}
	fseek(fn, 0, SEEK_SET);

	//segment-ek számának meghatározása (=sorok száma)
	while(!feof(fs)) { 		
		if(fgetc(fs) == '\n')
			Ns++;
	}
	fseek(fs, 0, SEEK_SET);

	//printf("Nn: %d\tNs: %d\n", Nn, Ns);

	Node* NOD = (Node*) malloc(Nn*sizeof(Node));
	Segment* SGM = (Segment*) malloc(Ns*sizeof(Segment));

	int in, is;

	//node-ok beolvasása a tömbbe
	for(int i = 0; i < Nn; i++) {
		fscanf(fn, "\n%d", &in);
		//printf("%d", in);
		fscanf(fn, "\t%f\t%f\t%d", &NOD[in].x, &NOD[in].y, &NOD[in].NumSegm);
		//printf("\t%f\t%f\t%d", NOD[in].x, NOD[in].y, NOD[in].NumSegm);

		for(int j = 0; j < NOD[in].NumSegm; j++) {
			fscanf(fn, "\t%d", &NOD[in].SgmList[j]);
  			//printf("\t%d", NOD[in].SgmList[j]);
		}
		//printf("\n");
	}

	//segment-ek beolvasása a tömbbe
	for(int i = 0; i < Ns; i++) {
		fscanf(fs, "%d", &is);		
		double l = SGMlength(NOD[SGM[is].N1], NOD[SGM[is].N2]);
		fscanf(fs, "\t%d\t%d\t%d\n", &SGM[is].N1, &SGM[is].N2, &SGM[is].type);
		//printf("\t%d\t%d\t%d\t%f\t%d\n", SGM[is].N1, SGM[is].N2, SGM[is].type, SGMlength(NOD[SGM[is].N1], NOD[SGM[is].N2]), duration(SGMlength(NOD[SGM[is].N1], NOD[SGM[is].N2]), SGM[is].type));
	}

	int Ns2;
	int is2 = 0;
	Segment2* SGM2 = (Segment2*) malloc(Ns*sizeof(Segment2));	
	unsigned short act_node, act_sgm;
	float act_length;
	char* usedSGM = (char*) calloc (Ns, sizeof(char));
	Node2* NOD2 = (Node2*)malloc(Nn*sizeof(Node2));
	
	for (in = 0; in < Nn; in++)
		NOD2[in].NumChain = 0;

	for (is = 0; is < Ns; is++) {
		if (usedSGM[is] == 1)
			continue;		
		act_sgm = is;
		usedSGM[act_sgm] = 1;
		act_node = SGM[act_sgm].N1;
		SGM2[is2].NumSegm = 1;
		SGM2[is2].SgmList = (unsigned short*) malloc(sizeof(unsigned short));
		SGM2[is2].SgmList[SGM2[is2].NumSegm-1] = act_sgm;
		act_length = SGMlength(NOD[SGM[act_sgm].N1], NOD[SGM[act_sgm].N2]);
		SGM2[is2].Length = act_length;
		SGM2[is2].Duration = SGMduration(act_length, SGM[act_sgm].type);

		for(int j = 0; j < 2; j++) {
			while(NOD[act_node].NumSegm == 2){				
				if (NOD[act_node].SgmList[0] != act_sgm) {					
					act_sgm = NOD[act_node].SgmList[0];
					usedSGM[act_sgm] = 1;
				}
				else {
					act_sgm = NOD[act_node].SgmList[1];
					usedSGM[act_sgm] = 1;
				}

				if (SGM[act_sgm].N1 != act_node)
					act_node = SGM[act_sgm].N1;
				else
					act_node = SGM[act_sgm].N2;
				SGM2[is2].NumSegm++;
				SGM2[is2].SgmList = (unsigned short*) realloc(SGM2[is2].SgmList, SGM2[is2].NumSegm*sizeof(unsigned short));		
				SGM2[is2].SgmList[SGM2[is2].NumSegm-1] = act_sgm;
				act_length = SGMlength(NOD[SGM[act_sgm].N1], NOD[SGM[act_sgm].N2]);
				SGM2[is2].Length += act_length;
				SGM2[is2].Duration += SGMduration(act_length, SGM[act_sgm].type);					
			}
			if (j == 0) {
				SGM2[is2].N1 = act_node;
				NOD2[act_node].ChainList[NOD2[act_node].NumChain] = is2;
				NOD2[act_node].NumChain++;
				act_node = SGM[is].N2;
			}
			else {
				SGM2[is2].N2 = act_node;
				NOD2[act_node].ChainList[NOD2[act_node].NumChain] = is2;
				NOD2[act_node].NumChain++;
			}
		}
		is2++;		
	}		
	free(usedSGM);
	Ns2 = is2;	
	SGM2 = (Segment2*) realloc(SGM2, Ns2*sizeof(Segment2));
	
	/*Segment2-k kiírása
	for (is2 = 0; is2 < Ns2; is2++) {
		printf("%d. N1: %d, N2: %d NumSegm: %d Hossz: %f Ido: %f Szegmensek: ", is2, SGM2[is2].N1, SGM2[is2].N2, SGM2[is2].Length, SGM2[is2].Duration, SGM2[is2].NumSegm);
		for (int i = 0; i < SGM2[is2].NumSegm; ++i)
			printf("%d ", SGM2[is2].SgmList[i]);
		printf("\n");
	}*/

	/* Node2-k kiírása
	for (in = 0; in < Nn; in++){
		printf("%d. %d", in, NOD2[in].NumChain);
		for (int i = 0; i < NOD2[in].NumChain; i++) {
			printf("\t%d", NOD2[in].ChainList[i]);
		}
		printf("\n");
	}*/

	Path** PTH = (Path**) malloc(Nn* sizeof(Path*));	
	for (int i = 0; i < Nn; i++)
		PTH[i] = (Path*) malloc(sizeof(Path)*Nn);		
	
	int Np = 0;					
	for (int i = 0; i < Nn; i++) {				
		for (int j = 0; j < Nn; j++){
			if (NOD2[i].NumChain == 0 || NOD2[j].NumChain == 0)
				PTH[i][j].Duration = -1;
			else if (i == j)
				PTH[i][j].Duration = 0;
			else {
				Np++;
				PTH[i][j].Start = i;	
				PTH[i][j].Duration = 100000;
				PTH[i][j].NSegm = 0;
				PTH[i][j].SgmList = (unsigned short*) malloc(0);
				PTH[i][j].Length = 0;			
			}
		}		
	}

	for (int i = 0; i < Nn; i++){
		if (PTH[i][i].Duration == -1)			
			continue;			
		
		char* used = (char*) calloc(Nn, sizeof(char));
		in = i;	
		while(in != -1){				
			for(int j = 0; j < NOD2[in].NumChain; j++){
				unsigned short act_sgm_in = NOD2[in].ChainList[j];		
				Segment2 act_sgm = SGM2[NOD2[in].ChainList[j]];
				if (act_sgm.N1 == in)
					act_node = act_sgm.N2;			
				else
					act_node = act_sgm.N1;
				if (used[act_node] == 1)
					continue;			
				if (PTH[i][act_node].Duration >= (PTH[i][in].Duration + act_sgm.Duration)) {
					PTH[i][act_node].Duration = PTH[i][in].Duration + act_sgm.Duration;
					PTH[i][act_node].Length = PTH[i][in].Length + act_sgm.Length;
					PTH[i][act_node].NSegm = PTH[i][in].NSegm + act_sgm.NumSegm;
					PTH[i][act_node].SgmList = (unsigned short*) realloc (PTH[i][act_node].SgmList, PTH[i][act_node].NSegm * sizeof(unsigned short));
					int l;
					for (l = 0; l < PTH[i][in].NSegm; l++)
						PTH[i][act_node].SgmList[l] = PTH[i][in].SgmList[l];
					for (int k = 0; k < act_sgm.NumSegm; k++) 					
						PTH[i][act_node].SgmList[l+k] = act_sgm.SgmList[k];									
				}
			}
			used[in] = 1;		
			in = next(PTH[i], Nn, used);
		}
		free(used);
	}
	
	/*metrikák kiírása
	for (int i = 0; i < Nn; i++){
		for (int j = 0; j < Nn; j++){
			float act = PTH[i][j].Duration;
			if (act == -1 || act == 0)
				printf("%0.0f\t", PTH[i][j].Duration);
			else
				printf("%f\t", PTH[i][j].Duration);			
		}
		printf("\n");
	}*/
	
	/*adott csúcsból induló útvonalak információnak kiírása
	for (int i = 0; i < Nn; i++){
		if (PTH[6][i].Duration == -1 || PTH[6][i].Duration == 0)
			continue;
		printf("%d to %d\nDuration: %f\nLength: %f\nNsegm: %d -", PTH[6][i].Start, i, PTH[6][i].Duration, PTH[6][i].Length, PTH[6][i].NSegm);
		for (int j = 0; j < PTH[6][i].NSegm; j++)
		{
			printf("\t%d", PTH[6][i].SgmList[j]);
		}
		printf("\n");
	}*/

	Path* PTH2 = (Path*) calloc(Np, sizeof(Path));	
	srand(time(NULL));
	for (int i = 0; i < Nn; i++){
		for (int j = 0; j < Nn; j++){			
			if (PTH[i][j].Duration == 0 || PTH[i][j].Duration == -1)
				continue;
			else {
				int n;
				while (PTH2[(n = rand()%Np)].Duration != 0);
				PTH2[n] = PTH[i][j];									
			}
		}
	}
	free(PTH);

	FILE* fp = fopen("routes.pth", "w");
	for (int i = 0; i < Np; i++){
		fprintf(fp, "\n%d\t%d", PTH2[i].Start, PTH2[i].NSegm);
		for (int j = 0; j < PTH2[i].NSegm; j++)
  				fprintf(fp, "\t%d", PTH2[i].SgmList[j]);
	}

	fclose(fp);
	fclose(fs);
	fclose(fn);

	float average_time = 0, average_lenght = 0, dispersion_time, dispersion_length;
	for (int i = 0; i < Ns2; i++) {
		average_time += SGM2[i].Duration;
		average_lenght += SGM2[i].Length;
	}
	average_time /= Ns2;
	average_lenght /= Ns2;

	for (int i = 0; i < Ns2; i++) {
		dispersion_time += pow(SGM2[i].Duration - average_time, 2);
		dispersion_length += pow(SGM2[i].Length - average_lenght, 2);
	}

	dispersion_time = sqrt(dispersion_time/Ns2);
	dispersion_length = sqrt(dispersion_length/Ns2);
	printf("Átlagos idő: %f\tszórás: %f\nÁtlagos hossz: %f\tszórás: %f\n", average_time, dispersion_time, average_lenght, dispersion_length);

	int* used = (int*) calloc(Ns, sizeof(int));
	for (int i = 0; i < Np; i++) {		
		for (int j = 0; j < PTH2[i].NSegm; j++) {
			used[PTH2[i].SgmList[j]]++;
		}
	}

	printf("Szegmensek előfordulásainak száma az útvonalakban:\n");
	for (int i = 0; i < Ns; i++) {
		printf("%d ", used[i]);
	}
	printf("\n");

	free(used);
	free(NOD);
	free(NOD2);
	free(SGM);
	free(SGM2);
	free(PTH2);
	return 0;
		
}