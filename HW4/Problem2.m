e = 0.001

n = 0:200
p = (1-e).^n+n.*(1-e).^(n-1)*e

plot(n,p)

p(150)

title('Probability of at most one bag failing while producing n bags')
xlabel('Bags')
ylabel('Probability')