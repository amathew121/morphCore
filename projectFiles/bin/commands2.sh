mv ../in/input.txtfst ../FST/input.txtfst
cd ../FST
echo Compiling Input 
fstcompile --isymbols=in.syms --osymbols=out.syms input.txtfst input.fst
fstdraw --isymbols=in.syms --osymbols=out.syms input.fst input.dot
dot -Tpng input.dot > PNG/input.png

echo Composing With morphCore
fstcompose input.fst final.fst | fstproject --project_output | fstrmepsilon > comp.fst
fstdraw --osymbols=out.syms comp.fst comp.dot
dot -Tpng comp.dot > PNG/result.png

echo Printing fst of shortest path
fstshortestpath comp.fst | fsttopsort | fstprint --osymbols=out.syms > comp.txtfst

fstinfo comp.fst

echo Moving files to out.

mv comp.txtfst ../out/comp.txtfst
mv PNG/result.png ../out/result.png

echo Done
