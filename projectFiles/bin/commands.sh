cd ../FST
mkdir -p PNG

echo Creating punctuation 

#cat ascii.syms in.syms > out.syms
#mv out.syms in.syms
#cat ascii.syms out.syms > out.syms
#mv out.syms out.syms

echo Compiling FST
fstcompile --isymbols=in.syms --osymbols=out.syms suffixRules.txtfst suffixRules.fst
fstdraw --isymbols=in.syms --osymbols=out.syms suffixRules.fst suffixRules.dot
fstcompile --isymbols=in.syms --osymbols=out.syms letters.txtfst letters.fst
#fstdraw --isymbols=in.syms --osymbols=out.syms letters.fst letters.dot
#fstcompile punct.txtfst punct.fst
fstcompile --isymbols=in.syms --osymbols=out.syms punt.txtfst punt.fst

echo Optimizing FST
#fstrmepsilon suffixRules.fst | fstdeterminize | fstminimize >suffixRules_opt.fst
fstrmepsilon suffixRules.fst suffixRules_opt.fst
fstdraw --isymbols=in.syms --osymbols=out.syms suffixRules_opt.fst suffixRules_opt.dot

echo Finalizing FST
#fstconcat letters.fst suffixRules_opt.fst  | fstconcat - punct.fst | fstclosure > morphCore.fst
fstconcat letters.fst suffixRules_opt.fst  | fstclosure > morphCore.fst
fstdraw --isymbols=in.syms --osymbols=out.syms morphCore.fst morphCore.dot
fstconcat morphCore.fst punt.fst  | fstclosure > final.fst
fstdraw --isymbols=in.syms --osymbols=out.syms final.fst final.dot

echo Drawing FST

#dot -Tpng letters.dot > PNG/letters.png
dot -Tpng suffixRules.dot > PNG/suffixRules.png
dot -Tpng suffixRules_opt.dot > PNG/suffixRules_opt.png
dot -Tpng morphCore.dot > PNG/morphCore.png
dot -Tpdf final.dot > PNG/final.pdf

echo INFO

fstinfo morphCore.fst

echo Done.

