library(ggplot2)

#--------------------------------------------------------------------------------------------
#Parametres globaux
dataset_name = "insurance"
taille_img_x = 1024/2
taille_img_y = 720/2
#fin parametres globaux
#--------------------------------------------------------------------------------------------

#--------------------------------------------------------------------------------------------
#data
#result

# oracle
nom = "oracle"
# pas contraintes
oracle = c(0.792, 0.7972, 0.79975, 0.80885, 0.80565, 0.81135, 0.8195, 0.8261, 0.83185, 0.83085, 0.8394, 0.8423, 0.848, 0.85355, 0.86005, 0.8689, 0.8704, 0.88105, 0.8909, 0.89185, 0.90575, 0.9104, 0.91335, 0.92245, 0.92825, 0.9308, 0.93345)
# contraintes
Naif = c(0.78445, 0.7875731289086141, 0.7847084540694423, 0.7864097570974685, 0.7882553984775517, 0.7967047291308201, 0.7909723686985868, 0.7989625238196062, 0.8039993566718491, 0.8120462489916644, 0.8097047329570126, 0.8167487684729065, 0.8266791662071248, 0.8296736894769026, 0.8382771912183676, 0.8457714253432009, 0.8500113973102348, 0.8591196401116109, 0.8675010046500947, 0.8812579488958261, 0.883212954333644, 0.8947739283617147, 0.8969497114591921, 0.9069352825815653, 0.9111641791044777, 0.9237039256817501, 0.9260417293007035)
# contraintes plus dur
jointree = c(0.7534, 0.7549471259553974, 0.7517139253002797, 0.7447208150720018, 0.7571530333610352, 0.7563426586440047, 0.7564168377823408, 0.7670360294605534, 0.7688234889637626, 0.7726946279804654, 0.7772777572851346, 0.7839292579777009, 0.786020651310564, 0.7995057660626029, 0.8087923279300687, 0.8079289732770746, 0.8082314881380301, 0.8251249768561377, 0.8263849229011994, 0.8277642474427667, 0.8372769199919792, 0.8506004477915734, 0.8506929861402772, 0.8539398512450146, 0.8545474325500435, 0.8690892675770229, 0.8735460094437406)

# DRC
nom = "DRC"
oracle = c(0.78445, 0.79405, 0.79745, 0.80265, 0.806, 0.81255, 0.81435, 0.82455, 0.82845, 0.83225, 0.8327, 0.8375, 0.8385, 0.8469, 0.84415, 0.85065, 0.84945, 0.85705, 0.8577, 0.86515, 0.8606, 0.8656, 0.8722, 0.87035, 0.8713, 0.87685, 0.88145)
Naif = c(0.7777, 0.7828257361839451, 0.7830399837216401, 0.7895653956539566, 0.7891494538489413, 0.7837315071357625, 0.7923742221284674, 0.7997777072086376, 0.8001612469766192, 0.8001077876583131, 0.8070289973342037, 0.8126095530236634, 0.8131983048048874, 0.8174470457079153, 0.8180596011651355, 0.8247706938270215, 0.8324133614067034, 0.8375622887908815, 0.8367605552675537, 0.8411117544875507, 0.8384302325581395, 0.8458330886239502, 0.8527614571092832, 0.8541232509675498, 0.8545422095306086, 0.8533476934123138, 0.8583388544238993)
jointree = c(0.75855, 0.7504842678393802, 0.7556681927185525, 0.751946354492243, 0.7536942901052134, 0.756835333415811, 0.7511580030880083, 0.7588046958377801, 0.7718500137854977, 0.7649569557983071, 0.7700202262341749, 0.7789099891925274, 0.7798947704081632, 0.7854254353387802, 0.7836567227968202, 0.7890117605757416, 0.7995489400090212, 0.8047897521581732, 0.7997902564591477, 0.8014143175433498, 0.8068535825545171, 0.8123318849575832, 0.8170428660178091, 0.8229659922513991, 0.8219820999781707, 0.8259205044476974, 0.8260771231482529)


# RB
nom = "RB"
oracle = c(0.792, 0.79695, 0.79935, 0.80745, 0.80345, 0.8103, 0.8182, 0.8247, 0.82755, 0.82715, 0.8354, 0.8346, 0.83915, 0.84035, 0.8473, 0.84655, 0.8494, 0.8549, 0.86025, 0.85895, 0.8686, 0.87265, 0.86935, 0.8734, 0.87485, 0.8757, 0.87805)
Naif = c(0.78445, 0.7884809360500302, 0.7838950739667531, 0.7853848519011991, 0.7875304230749314, 0.7964961676834037, 0.7884939886099979, 0.796262968452255, 0.8007827159170107, 0.8086044635654746, 0.8035171515414676, 0.8097974822112753, 0.8148229844491012, 0.8177775307132137, 0.8211273113233898, 0.822043952319078, 0.8275586961477092, 0.8346905073742953, 0.8320799127389632, 0.8435079199907504, 0.842497670083877, 0.8491485613623018, 0.8456601107054529, 0.8579069352825816, 0.8581492537313433, 0.8599340725202278, 0.8613432746076604)
jointree = c(0.7534, 0.7552088786514501, 0.751330006033017, 0.7444931413284763, 0.7565594206339784, 0.7569009366664599, 0.7523742299794661, 0.7634529891845265, 0.7644915079419652, 0.7671646078713014, 0.7699004057543342, 0.7790080738177624, 0.7782366957903097, 0.7874794069192751, 0.7931766103708733, 0.7937763713080169, 0.7935837526959022, 0.8062395852619885, 0.807824100513992, 0.8039941548952753, 0.8123120112291959, 0.8225117036433951, 0.8200335993280135, 0.818368006898782, 0.8160356832027851, 0.8326373998420042, 0.8312795116895082)

#fin data

#--------------------------------------------------------------------------------------------


#--------------------------------------------------------------------------------------------
#parametres graphes
# -- manuels
x_pas_erreur = 4
y_pas_erreur = 3
x_legend_erreur = 0
y_legend_erreur = 50
y_padding_erreur = 1

x_pas_time = 4
y_pas_time = 3
x_legend_time = 0
y_legend_time = 0.013
y_padding_time = 0.01

# -- auto calcul

#MAJ VERS ERROR
oracle = 100-100*oracle
Naif = 100-100*Naif
jointree = 100-100*jointree
wmv = 100-100*wmv
vpop = 100-100*vpop
vnaif = 100-100*vnaif

nb_val = length(oracle)
nb_val = length(Naif)
nb_val = length(jointree)
nb_val = length(wmv)
nb_val = length(vpop)
nb_val = length(vnaif)

min_val_erreur = 30000
min_val_erreur = min(min_val_erreur,100-100*oracle)
min_val_erreur = min(min_val_erreur,100-100*Naif)
min_val_erreur = min(min_val_erreur,100-100*jointree)
min_val_erreur = min(min_val_erreur,100-100*wmv)
min_val_erreur = min(min_val_erreur,100-100*vpop)
min_val_erreur = min(min_val_erreur,100-100*vnaif)
min_val_erreur = ceiling(min_val_erreur)

max_val_erreur = 30000
max_val_erreur = max(max_val_erreur,100-100*oracle)
max_val_erreur = max(max_val_erreur,100-100*Naif)
max_val_erreur = max(max_val_erreur,100-100*jointree)
max_val_erreur = max(max_val_erreur,100-100*wmv)
max_val_erreur = max(max_val_erreur,100-100*vpop)
max_val_erreur = max(max_val_erreur,100-100*vnaif)
max_val_erreur = floor(max_val_erreur)

size = 0:(nb_val-1)
x_lim_erreur = c(0,(nb_val-1))
if(min_val_erreur-y_padding_erreur <= 0){
  min_val_erreur = min_val_erreur+y_padding_erreur+0.00001
}
y_lim_erreur = c((min_val_erreur-y_padding_erreur),(max_val_erreur+y_padding_erreur))
x_axp_erreur = c(0, (nb_val-1), x_pas_erreur)
y_axp_erreur = c((min_val_erreur-y_padding_erreur),(max_val_erreur+y_padding_erreur),y_pas_erreur)

#fin parametres graphes
#--------------------------------------------------------------------------------------------

(ggplot(NULL, aes(size)) + #scale_y_log10(breaks = round(seq(0, 100, by = 5),1)) + annotation_logticks(sides="l") +
    ylab("Error rate (%)") + xlab("Number of assigned variables") + theme_bw() #+ theme(legend.position="bottom")
  +geom_line(aes(y=Naif, colour="Tightness 5%"), colour="turquoise2", linetype = "dotted") + geom_point(aes(y=Naif, shape="Tightness 5%"), colour="turquoise2", fill="turquoise2") 
  +geom_line(aes(y=jointree, colour="Tightness 10%"), colour="springgreen4", linetype = "dotted") + geom_point(aes(y=jointree, shape="Tightness 10%"), colour="springgreen4", fill="springgreen4") 
  +geom_line(aes(y=oracle, colour="No constraint"), colour="black", linetype = "dotted") + geom_point(aes(y=oracle, shape="No constraint"), colour="black", fill="black") 
  + scale_colour_manual(name = 'Legend', guide = 'legend',
                        limits = c(NULL
                                   ,'Tightness 10%' #jointree
                                   ,'Tightness 5%' #Naif
                                   ,'No constraint' #oracle
                        ),
                        values =c(NULL
                                  ,'Tightness 10%'='springgreen4' #jointree
                                  ,'Tightness 5%'='turquoise2' #Naif
                                  ,'No constraint'='black' #oracle
                        ))
  + scale_shape_manual(name = 'Legend', guide = 'legend',
                       limits = c(NULL
                                  ,'Tightness 10%' #jointree
                                  ,'Tightness 5%' #Naif
                                  ,'No constraint' #oracle
                       ),
                       values =c(NULL
                                 ,'Tightness 10%'=21 #jointree
                                 ,'Tightness 5%'=24 #Naif
                                 ,'No constraint'=4 #oracle
                       ))
  
  + guides(shape = guide_legend(override.aes = list(colour = c(NULL
                                                               ,'Tightness 10%'='springgreen4' #jointree
                                                               ,'Tightness 5%'='turquoise2' #Naif
                                                               ,'No constraint'='black' #oracle
  ),
  fill = c(NULL
           ,'Tightness 10%'='springgreen4' #jointree
           ,'Tightness 5%'='turquoise2' #Naif
           ,'No constraint'='black' #oracle
  ))))
)


