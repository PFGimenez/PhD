#!/env/python
# -*- coding: utf-8 -*-

from random import *
from math import *

def lineaire(y,n):
# f(x) y = (2^n-((x-1)^2/2)*2/(2^n*(2^n+1))-2^(n+1)/(2^n*(2^n+1))
# f-1(y): x = sqrt(-(y+(2^(n+1)/(2^n*(2^n+1))))*2^n*(2^n+1)+2^(n+1))+1
    return sqrt(-(y+(2**(n+1)/(2**n*(2**n+1))))*2**n*(2**n+1)+2**(n+1))+1;

def uniforme(y,n):
#
#
    return 2**n*y+1;

def random(func,n):
    y = uniform(0,1);
    return int(floor(func(y,n)));

print "p:";
p = int(input());
print "\nn:";
n = int(input());
print "\nf:";
f = input();

for i in range(p):
    NB = random(f,n)
    print repr(NB);
