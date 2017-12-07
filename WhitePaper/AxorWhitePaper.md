{% include mathjax.html %}

Copyright (c) 2016-17, Axor Foundation
======================================

All Rights Reserved
===================

Objective
---------

Create a protocol for axors, which are smart contracts on an Ethereum-like blockchain which each track an unambiguously defined price. An example of a price definition for an individual axor might be "the latest Zillow zestimate of house at address xyz." Axors have two sets of parties -- one set that buys the positive side of the axor (a paxor), and one that buys the negative side of the axor (naxor). The cryptocurrency used to make the transactions is called axo. If party A sends *L*<sub>*n* − 1</sub> axos to a paxor on a given axor, and likewise party B sends *S*<sub>*n* − 1</sub> axo to the naxor on the same axor as party A, then after some time a price update occurs, changing the price of the axor from *P*<sub>*n* − 1</sub> to *P*<sub>*n*</sub>, party A should be able to receive, if desired, after one timestep:

$$L\_n = \\frac{P\_n}{P\_{n-1}} \* L\_{n-1}$$
 Where the current price 0 &lt; =*P*<sub>*n*</sub> &lt; =2 \* *P*<sub>*n* − 1</sub>.

In Appendix A, we derive the formula for the updated short balance *s*<sub>*n*</sub>:

$$s\_n = (2-\\frac{P\_n}{P\_{n-1}})\*S\_{n-1}$$

Both of the above formulas are valid in the range for any single timestep:

0 &lt; =*P*<sub>*n*</sub> &lt; =2 \* *P*<sub>*n* − 1</sub>

### Objective Implications Analysis

Similar relations hold true for an axor which has multiple paxors:

$$\\sum\\limits\_{i=1}^k L\_{i,n} = \\frac{P\_n}{P\_{n-1}}\*\\sum\\limits\_{i=1}^k L\_{i,n-1}$$

and multiple naxors:

$$\\sum\\limits\_{i=1}^k S\_{i,n} = \\frac{P\_n}{P\_{n-1}}\*\\sum\\limits\_{i=1}^k S\_{i,n-1}$$

In Appendix B, we show that

$$L\_n = \\frac{P\_n}{P\_0}\*L\_0$$

Assuming that for all *n* ∈ (0, *n*), 0 &lt; =*P*<sub>*n*</sub> &lt; =2 \* *P*<sub>*n* − 1</sub>.

There is no known closed form for *s*<sub>*n*</sub> in terms of *s*<sub>0</sub>, the initial balance. We only have the recurrent relation defined above.

Naive Contract
--------------

If party A wants to buy into a paxor on an axor x which tracks price y, and party B wants to buy into a naxor on X, then one naÃ¯ve contract (NC) would be one where A and B deposit an equal amount into the NC. Then when some deadline is reached, A and B are paid proportional to the change in y from the time the contract was entered until the deadline.

For example, suppose A and B each send 100 axos to x, and then y increases 10% at the deadline. Then the NC would pay A 110, and B 90. This contract is based on one described in the Ethereum White Paper. My disclaimer is that I'm not labeling this contract as naÃ¯ve because I think Vitalik Buterin was naive in proposing it; rather, I'm labeling it naive for the purpose of enabling users in the main use case, which is to allow the user to buy into a paxor and have their paxor axo balance increase or decrease according proportional to changes in price y.

### Problems with the Naive Contract (NC)

The first problem is: what happens if S goes up more than 100%? if the price went up 150%, for example, A should receive 250. However, NC will have insufficient funds to pay A 250, as only 200 were originally deposited.

The other problem with NC is usability. If your goal is to hold your side of the contract for a long time, having a deadline means that the user (or at least the app) must remember to take the cryptocurrency received at the deadline, and then put it back in a new contract. There are many opportunities for error in this process, so it is not an experience to which I would like to subject our users. Their exposure to the price represented by the contract should last for years, if desired.

Long Term Contract (LTC)
------------------------

I propose a long term contract (LTC) as one similar in some ways to the NC, but without a deadline at all. Instead, the contract allow many people to take the positive (paxor) and negative (naxor) side of the axor, and the axor contract will make appropriate adjustments over time to keep the paxor and naxor positions balanced in terms of liabilities, which are the amounts due to the contract parties at any given time.

### Rebalancing the LTC

After each of the n timesteps, the naxors and paxors must be rebalanced; this is so that as long as the price does not double within a single timestep, the paxors will be able to pay out their full value

It should be possible to keep the naxors and paxors balanced using the following actions:

1.  Moving paxors to and from offer bucket (similarly for naxors)
2.  Changing the naxors' strike price when needed
3.  (We don't want to change the paxors' strike prices, as our paxor owners are our ultimate end users -- our naxor owners are more like market makers, so it should be more acceptable to have a little more complicated behavior)

We will simulate and experiment to understand what combination of the above strategies will optimize the amount of axos placed in an axor over time as the price, the naxors, and the paxors change over time.

### Offer Buckets

You can think of the contract as the following buckets of cryptocurrencies: 1. Long positions 1. Short positions 1. Long offers 1. Short offers

Offers are submitted orders which have not yet been fully processed into a position, or also positions which have been kicked out because of an insufficient fee offer to stay. (More on fees later).

### LTC Example

Suppose that the contract is newly created at time T and is therefore empty of currency. Suppose that some parties want to take the long position on the security in the amount of 110; they will submit an offer and 110 will go into the long offers bucket. Party A may cancel their order at any time and receive their currency back. Suppose party B wants to take the short position in the amount of 100; they will submit an offer and 100 will go into the short offers bucket. Here is a table showing the status of the contract:

At Time T Underlying Security Price: $100.00

| Bucket    | Long | Short |
|-----------|------|-------|
| Positions | 0    | 0     |
| Offers    | 110  | 100   |

The contract will take the balanced proportion of the offers and move them into positions and this will be status at T':

| Bucket    | Long | Short |
|-----------|------|-------|
| Positions | 100  | 100   |
| Offers    | 10   | 0     |

Then suppose that at time T'' the price of the underlying security has changed to $90.00, which is a decrease of 10%. This means that the amount owed to the long and short parties has changed. Now 90 is owed to the long position, and 110 is owed to the short position, leaving a gap of 20 between the long and short buckets:

| Bucket    | Long | Short |
|-----------|------|-------|
| Positions | 90   | 110   |
| Offers    | 10   | 0     |

In order to balance the two sides, there are two available techniques:

1.  Use any available offer to bring the smaller position bucket up closer to the smaller position side
2.  Move some of the larger position side back down to the offer bucket, so that both positions are equal

In the above example we've been developing, the contract can and should do both. First it moves the available offer in the long bucket up to the position bucket, which results in the following state:

| Bucket    | Long | Short |
|-----------|------|-------|
| Positions | 100  | 110   |
| Offers    | 0    | 0     |

Then we move some of the short position back down to the offer bucket, yielding the following state:

| Bucket    | Long | Short |
|-----------|------|-------|
| Positions | 100  | 100   |
| Offers    | 0    | 10    |

As long as the price does not more than double between any price updates that the contract receives, the axor should be able to maintain the correct buckets to pay the correct liabilities to the long and short parties.

All of the above analysis holds true if there are multiple parties represented in the various buckets. The only difference is that we need to take the next step and take into account that typically more people will want to take the long position than the short. Answering this question will help to determine which parties' positions and offers will get shifted between the buckets, and at what time. This question is answered in the section titled "Annual Fees."

In summary, the current balance of the long and short positions are maintained as equal every time axor's price is updated. This will be the behavior for the initial version of the axor protocol. A subsequent version may introduce leverage, which will mean the ratio between the long and short balances will be determined by the leverage ratio, but this is out of scope for this whitepaper at this time.

### Formula for Total Short Balances

The formula for the balance owed a short position for a current price 0 &lt; =*P*<sub>*n* + 1</sub> &lt; =2 \* *P*<sub>*n*</sub> and a strike price *P*<sub>*n*</sub> &gt; 0 is:

$$S\_{n} = (1 + \\frac{P\_{n-1} - P\_{n}}{P\_{n-1}}) \* s\_n$$

*P*<sub>*n* + 1</sub> is the current price of the underlying security, and *s*<sub>*n*</sub> is the balance of the short positoin at the time of the last price update, and *S*<sub>*n* + 1</sub> is the current balance owed to the short position holder. *P*<sub>*n*</sub> is the current strike price of the short position. If we rewrite the above equation in terms of *s*<sub>0</sub> and *P*<sub>0</sub>, we have:

$$s\_n = \_n{(1 + )}\*s\_0

Because the strike prices are continually updated as the balance is updated, the short position has a different value curve with respect to the original price *P*<sub>0</sub> at which the short position was entered and the price history than a for a contract for different (CFD) short position. As the price continually increases with multiple updates, the short position loses value at a smaller rate than it would if the strike price remained the same.

For a CFD (where the strike price remains static) the short position loses value at a linear rate when compared to

Since the new balance becomes invalid with a *P*<sub>2</sub> &gt; 2 \* *P*<sub>1</sub>, the contract will have to prevent this situation from occuring. There are multiple ways that this could be done:

1.  When *P*<sub>2</sub> &gt; =2 \* *P*<sub>1</sub>$, close the naxor, and rebalance the overall axor.
2.  Force naxor (short) position into a new, higher strike price once *P*<sub>2</sub> passes some threshold, say $\\frac{3}{2}\*P\_1$.
3.  Maintain the balance and other important operating statistics excluding those where the *P*<sub>2</sub> &gt; 2 \* *P*<sub>1</sub>.

The first two operations would be O(n), where n is the number of positions, which is unacceptable for the purposes of the contract. The third option can be done using a special version of an AVL tree, which is designed to return a sum of the tree where the sorting number is greater than or less than some number. In this case, we would have the AVL tree return the sums of node balances for all nodes where the price of the node is less than $\\frac{P\_2}{2}$.

### Protocol Operations

These operations will be detailed further in the black paper:

-   Create Axori
-   Create Axor
-   Offer
-   Cancel offer
-   List offers
-   List paxors, naxors
-   Withdraw
-   List operating statistics
-   Price history
-   Transaction history

#### Move Balance from Offer to Position

The offers and positions will each be arranged in separate AVL trees which sort the offers and positions by their fee price offer. The AVL tree also tracks the volume available above and below each offer / position in the tree.

First, the new market clearing price is established for the new target balance. This price is used to split the AVL tree that represents the offers into two parts -- one which will remain as offers and the other which is moved to the positions AVL tree under the current market price for the underlying security.

#### Single Block, more than 100% Price Increases

If there is an instantaneous, more than 100% price increase between price updates, there is no way to avoid some loss in balance for the long position holders. There are a few ways to approach this problem:

1.  Just readjust the balance so that the loss is shared equally among the long position holders (must be part of the terms of service of the contract and app that this will be the result if the price goes up 100% in between price updates, although rarely)
2.  Have some sort of insurance shared among all of the securities' contracts for this occurance. Doing so would require the payment of some insurance premium to an insurance ethereum contract

We should begin our securities rollout first with blue chip stocks and large index funds to make such an eventuality less likely.

### Annual Fees

It is reasonable to assume that there will be an imbalance in the demand for the naxors and paxors for a given axor. I believe our customers will be willing to pay a small annual fee if that is the only reasonable way to get access to the much larger average returns that are possible by buying into many of the axors than are available through simple bank lending, etc.

Paxor offers include the maximum annual fee they are willing to pay in order to stay in the position bucket. Likewise the naxor offers specify the minimum annual fee they want to stay in the naxor bucket.

In order to incentivize enough people to take the naxor position, there is an annual fee percentage, paid by the paxor position holders to the naxor position holders. The fee will be the market clearing price to keep the paxors and naxors balanced. The minimum of the maximum fees of all of the paxors are willing to pay must be greater than the maximum of the minimum fees that the naxors demand. This state must be maintained whenever offers are moved to positions, as well as when there are price updates. The actual fee paid by all of the paxors is the maximum of the minimum fees demanded by the naxors in the naxor (active, non-offer) bucket.

When chosing which offer to move to the paxor bucket, the contract will choose the one with the greatest maximum fee. Likewise, when choosing which offer to move to the naxor bucket, the one with the smallest minimum fee will be chosen first.

#### Fee payment mechanism

The annual fee will be paid each time the contract has received a verified price update -- therefore, the annual fee is compounded "semi-continuously." So, for example, the fee paid at a given time T', called *f*<sub>*T*</sub>′ will be:

$$f\_T' = \\frac{(T'-T)\*f}{Y}$$

When T is the time at which the fee was last paid, f is the current fee, and Y is one year, and T and T' are expressed in fractions of a year.

Since we anticipate that many parties will have positions for the long and short buckets, and because running any code in the contract is very expensive and requires paying Ethereum gas, we must ensure that the fee payment is done in constant time (i.e., the number of operations should not grow as the number of positions increases in the contract). For example, we do not want to resort to updating the account balances for every position each time the fee is paid. Therefore, we need to keep a universal value which captures how much fee is paid over time, including the fact that the annual fee may vary over time. Therefore, we shall keep a variable F which stores the amount of fees that have been charged the long position over time, and should be reflected when parties check their account balances or request withdrawals.

At contract creation, F is initialized to 1, and F is updated as follows each time the fee is charged at the current time T:

*F*′=*F* \* (1 + *f*<sub>*T*</sub>)

Where *f*<sub>*T*</sub> is the incremental annual fee defined above. When an offer is moved to a position bucket at timestamp T, the value of F at that time, F, is recorded along with other information for the position at that time. Then, at T', the balance L' able to be withdrawn for the long position is the following:

$$L' = \\frac{P'}{P} \* \[1-(F' - F)\]\*L$$

##### How to Calculate Effective L / S

Since the a naxor or paxor in a given axor has a minimum / maximum fee, it is possible that the paxor or naxor will go back and forth many times between being an offer and position. For example, for a naxor, if the market clearing annual fee dips below the offered minimum fee, then the position will move back to being an offer. Consequently, we need to keep track of the actual price movement of a naxor or paxor while it is a position and not when it is an offer (if we counted the price movement when it is merely an offer than we would be over-paying and we wouldn't be able to compensate other paxor and naxor contract holders sufficiently).

The question is -- how to do this efficiently, in O(log(n)) time, which is a requirement given the millions of positions we will have for some individual axors? The solution is to track the total price movements above and separately below for each market clearing annual fee. There will be a binary search tree for every market clearing annual fee that has ever been reached for the axor. Then for each market clearing price, there is another binary search tree that contains the time stamps when the axor's annual fee crossed the node's clearing price. Each timestamp node in this annual fee value's tree contains the total price movements which occured while the annual fee clearing price was above this node's annual fee value, and also separately the total price movements which occurred while the annual fee clearing price was below this node's annual fee value.

The market clearing annual fee is calculated after every block is completed for an axor and there is a price update that requires any rebalancing of the naxors and paxors. If the annual fee is a floating point number, a problem we can run into is that the new clearing price crosses over a lot of annual fee values, and therefore all of them have to be updated, which will cost a lot of gas. However, we can mitigate this problem by discretizing the allowed annual fee values. For example, we could allow any fee set between -100% and +100% in increments of 0.01, which will limit the number of possible values of the annual fees to k = 20,200 different values. If we were to do so we can store each annual fee value node in an array instead of a tree, which will simplify the operations needed. Assuming the number of values then are constant, then we are worst case O(k + log(n)) = O(log(n)), where n is the number of blocks (timestamps) processed).

Then when a naxor or paxor is withdrawn, we can use the above data structure to calculate the L / S (the effective initial balance of the paxor or naxor) using this data structure. For a naxor, we simply calculate the total price movements when the annual fee was above the naxor's minimum fee by finding the difference between the total price movements for the start and end timestamps in the above data structure.

### Stock dividends

Since the price updates only include the change in price of the security, how will long position holders earn the appropriate dividends for stocks, or interest payments for notes? The answer is the annual fee. The annual fee may become negative -- in other words, the long positions may end up receiving and the short positions may pay for the privilege for interest bearing or dividend paying underlying securities; alternatively, the dividend may serve to make the annual fee that the long position holders must pay the short position holders smaller.

#### Estimated Annual Fee

It is standard practice to price a contract by taking into account the costs associated with taking an equivalent set of positions in a portfolio which together have the same risk profile and the same price movements as the option. We can do the same analysis here to estimate the normal annual fee.

Someone with access to a mainstream brokerage account could take the short position using the contract proposed here, and simultaneously take a long position of equivalent value. If we make the assumption that the person trusts their investment bank who services their brokerage acocunt and the contract holding their short position, they will believe their investment has zero risk. This is because if the underlying security increases in the value, the short position in the contract will decrease by the same value, and vice versa. Therefore, the annual fee demanded by this hypothetical person will be the interest rate they could have earned had they invested the same amount of capital in a vehicle considered risk-free, such as treasury bills (putting my own misgivings about the long-term viability of treasury bills aside). This interest rate is labeled *I*. Then the annual fee f charged by the short position holders will be:

*f* = 2 \* *I* − *D*

Where D is the annual dividend paid by the stock, or the interest paid by a bond. The reason we have 2 next to the I is because to take the above risk-free position, the short position holder will have to invest twice the capital as the long position holder. As of Aug 30th, 2016, the last 4 week treasury bill auction went at an interest rate of 0.279%. The reason I chose the shortest possible term is because the proposed contract is a demand deposit, meaning that it can be withdrawn at any time (that the underlying exchange is open), and is therefore extremely short term in nature. Suppose that our underlying security were Microsoft stock. Microsoft is my day job, and the current annual dividend yield is 2.48%. Therefore, our estimated annual fee is:

*f* = 2 \* 0.279%−2.48%= − 1.92%

Since the annual fee is negative, the long position holders will actually receive a positive fee over time.

Please note that I have simplified the above analysis by ignoring differences in the compound interest rate. I have also ignored expectations regarding the change in price of the crypotocurrency used to trade and the risk associated with this expected volatility, and again any perceived risk in utilizing an avant garde smart contract.

### Axors with Many Naxors and Paxors

It should be possible to keep the naxors and paxors balanced using the following actions:

1.  Moving paxors to and from offer bucket (similarly for naxors)
2.  Changing the naxors' strike price when needed
3.  (We don't want to change the paxors' strike prices, as our paxor owners are our ultimate end users -- our naxor owners are more like market makers, so it should be more acceptable to have a little more complicated behavior)

As part of of our ongoing research, we will simulate and experiment to understand what combination of the above strategies will optimize the amount of axos placed in an axor over time as the price, the naxors, and the paxors change over time.

#### Examples

Suppose that our starting condition at T' is that the underlying security is at $100.00, and at that time a long position is taken, and a short position is taken, resulting in the following contract state:

#### Price of Axor Plummets

| Bucket    | Long | Short |
|-----------|------|-------|
| Positions | 100  | 100   |
| Offers    | 0    | 0     |

Then, suppose that at time T' the price of the underlying security has plummeted to $1 because of some bad news related to the security. This means the balance of the buckets will change to the following balances (ignoring fees):

| Bucket    | Long | Short |
|-----------|------|-------|
| Positions | 1    | 199   |
| Offers    | 0    | 0     |

You might think by looking at this, that the buckets are unbalanced, and that we had better increase the balance of the long position or decrease the balance in the short position in order to be able to pay future gains to the short position holder. Not so, for the strike position still has the same strike price as before, $100, and the minimum price possible for a security is $0, so the maximum that the position can earn is a doubling of the original amount invested. Therefore, the balances do not technically need to be changed in order to ensure that there is enough money to pay any withdrawls requested.

However, there is another reason to consider changing the balances -- the annual fees. Suppose that the short positions are currently earning an annual fee of 0.5%. This means that only 0.5 cents will be earned on that $1 balance in the long position. Also, since the price of the underlying security has already plummeted, so there is little opportunity for short position holders to earn any more. Therefore, it is likely that the short position holders will cash out their positions quickly after the price plummeted. The question is, how much can their balances cash out before we no longer have enough cash to pay out the long position movements? It would be anything less than $199, at least at the current strike price of $100.

We have previously assumed that we want enough cash in the short position to cover a doubling of the price to be able to pay out the long position. If the price of the underlying security in this scenario were to double to $2, then we would need to be able to justly transfer 1 currency unit from the short position bucket to the long position bucket. We are able to do so with the current short balance of 199 and a strike price of $100.00. The new balance would be 198 for the short position bucket.

### Market Opening and Closings

It is likely that the price tracked by a given contract will be specific to a specific exchange. It may be reasonable to only allow changes while the exchange is trading during normal hours, because the often limited liquidity after market hours may make it difficult to report an accurate price for the purposes of rebalancing the contract or allowing deposits and withdraws.

### Axori

Axori is a topic; it contains a set of axors. A topic can be a group of related prices with similar properties, such as volatility, update frequency, etc. For example, the axors of individual home prices according to Zillow.com's Zestimate could fall under an axori which is optimized for the U.S. domestic housing market. Axori parameters that could be optimized for this type of price are covered after axor mining is introduced in the next section.

### Whole Axori Mining Protocol

The security of our protocol is provided by the price updaters -- axor's miners; they mine blocks composed of a list of pending transactions. The miners take the list of transactions, look up the prices as defined by the axors' descriptions at the timestamp for each transaction, and then upload the block to the axori ethereum contract.

The timestamp is when the end user submitted the transaction to the respective axor. When the miner looks up the price for the transaction, it must not look up the current price, but instead look back in history to report the price at the timestamp.

The axori evaluates each miner's answers for accuracy, punishing miners by removing some axo when their reported prices deviate from the consensus prices for each of the transactions, and redistributing that axo to miners who reported the median prices. The amount of axo lost by innacurate miners is proportional to the amount of axo deposited into the axori as well as the degree to which their price reports in the block are innacurate. Miners also share in the transaction fees. Their share is a function of their accuracy and amount of axo deposited into the axori.

It is important to note that if a miner reports any prices, it must report prices for every transaction in their block. If a miner does not include all of the transactions in the block, their block is deemed invalid and ignored. Suppose then that a new axor is added to the axori, and in the beginning the weighted (by axo) majority of the miners do not know how to look-up the price for the new axor. In that case, the majority will return "unknown" for the price, and the transaction will fail, having no effect on the miners' punishment or rewards.

If miners are copying the prices correctly, they should in theory be able to match the consensus block perfectly. If that is truly the case (and more testing may reveal if this is a practical expectation), then it seems reasonable to simply reward perfect block miners with their share of the transaction fees, and then redistribute some of the axos from the non-perfect block miners to the perfect-block miners (proportional to their deviance from the consensus block as well as size of their axo allocation to the axori).

#### Consensus

There are many ways a consensus block can be formed from the blocks submitted by the miners. One simple way is to just set the consensus for each transaction to one of the following:

1.  The weighted median price
2.  The most price with the most axo weight
3.  Best blocks' prices

TODO: define the weighted median price

\#3 would work as follows:

1.  Rank the blocks according to how many median or most frequent prices the block matches.
2.  Then, use the weighted median or most axo weight price among the blocks which are tied for first place.

\#3 has the advantage of trusting the blocks with the best overall accuracy on the block with determining the transactions.

#### Axori Mining Parameters

Axori parameters that could be optimized for this type of price include:

-   Block size: the number of transactions
-   Block frequency: how often blocks are issued (daily in the case of housing)?
-   Transaction fee percentage: will influence how many miners are willing to compete to secure the axori's transactions
-   Axori market creator percentage (what percentage of the transaction fees does the axori creator receive)?
-   Axor market creator percentage (what percentage of the transaction fees for an individual axor does the axor creator receive)?
-   Axor creation allowed (are people other than the axori creator allowed to create axors within this axori)?

As the axori creators will be competing with each other to create the best sources of income for themselves, they are likely to explore many alternatives for these parameter values and eventually converge on values that maximize the total utility of the axori for participants.

#### 51% Attack

Like all blockchain applications, axor is vulnerable to a 51% attach. In our case it would be where a fraudulent actor has control of the majority of axos for a particular axori. For this reason it is imperitavite that the axori's transaction fees be set high enough to attract enough miners with enough axo to truly secure the axori.

The Augur team (add citation) show in their simiulations that they can have 75% liars and still in the long term transfer their reputation coin over to honest reporters. They claim this based on a clustering algorithm which favors tight primary clusters of reports over outliers and smaller secondary clusters. However, I believe the 75% claim breaks down once you realize that a single fraudulent actor could create many reporter accounts, which could confuse the clustering algorithm. Once you make this realization, it seems clear that any fraudulent actor could use a majority of axo to create fraudulent blocks.

### Vulture Mining Protocol

Suppose that we have implemented the above mining protocol; in that above protocol, a certain number of miners will have invested a , and there is a fradulent actor or group of actors.

### Mutual Funds and ETFs

Since ETFs are traded like any stock, they could easily be tracked just like any other stock by the proposed smart contract. Mutual funds are similar, except that price/NAV updates would only happen once a day. In addition to tracking existing mutual funds, we can also allow the creation of meta-contracts which track a basket of stocks and other securities that are not reflected in any existing mutual funds. This is similar to the idea behind the founding of [kaching.com](http://www.cbsnews.com/news/kaching-takes-on-mutual-fund-industry-19-10-2009/), where a friend of mine, (Jared Jacobs), worked, long before it became [Wealthfront](http://venturebeat.com/2010/10/19/kaching-wealthfront/). My guess is they lost the battle with regulators to democratize investing by allowing anyone to create their own virtual mutual fund; our approach may be similar, but it would be under the fully decentralized, unstoppable model of Ethereum-based applications.

Conclusion
----------

Axors are going to break down barriers between disparate financial markets and untapped capital. Join us in the work to make the world one financially.

Appendix A: Deriving Formula for Short Balance
----------------------------------------------

We must define the short balance after some time in terms that will allow the change in the short balance to exactly equal the change in the long balance. Otherwise, the smart contract will either have a shortage or excess of cryptocurrency which does not have a defined owner. In other words,

$$\\frac{dL}{dP} = -\\frac{dS}{dP}$$

for an axor.

Using the formulas for L defined in the objective section at the start of the white paper, we can rewrite the above differential equation as:

$$\\frac{d}{dP}L = \\frac{L\_n - L\_{n-1}}{P\_n-P\_{n-1}} = \\frac{\\frac{P\_n}{P\_{n-1}}\*L\_{n-1} - L\_{n-1}}{P\_n-P\_{n-1}} = \\frac{L\_{n-1} \* (\\frac{P\_n}{P\_{n-1}} - 1)}{P\_n-P\_{n-1}} = -\\frac{dS}{dP}$$

Likewise,

$$-\\frac{dS}{dP} = -\\frac{d}{dP}S = -\\frac{s\_n-S\_{n-1}}{P\_n-P\_{n-1}} = \\frac{L\_{n-1} \* (\\frac{P\_n}{P\_{n-1}} - 1)}{P\_n-P\_{n-1}}$$
 The *P*<sub>*n*</sub> − *P*<sub>*n* − 1</sub> terms cancel, leaving us with:

$$S\_{n-1}-s\_n = L\_{n-1} \* (\\frac{P\_n}{P\_{n-1}} - 1)$$

Since we are assuming that the long and short positions were balanced (equal) at the last timestep *n* − 1, this simplifies to:

$$s\_n = S\_{n-1} - S\_{n-1}\*(\\frac{P\_n}{P\_{n-1}}-1) = S\_{n-1} \* (1 - (\\frac{P\_n}{P\_{n-1}}-1)) = S\_{n-1}\*(2 - \\frac{P\_n}{P\_{n-1}})$$

And we conclude:

$$s\_n = (2-\\frac{P\_n}{P\_{n-1}})\*S\_{n-1}$$
 Where:
0 &lt; =*P*<sub>*n*</sub> &lt; =2 \* *P*<sub>*n* − 1</sub>

Appendix B: Formula of *L*<sub>*n*</sub> in terms of *L*<sub>0</sub>
--------------------------------------------------------------------

$$L\_{n} = \\frac{P\_{n}}{P\_{n-1}}\*L\_{n-1}$$
 We can solve for *L*<sub>*n*</sub> in terms of *L*<sub>0</sub>, the initial balance, *P*<sub>0</sub>, the initial price, and *P*<sub>*n*</sub>, the current price, as follows:

First, the initial step is:

$$L\_1 = \\frac{P\_1}{P\_0}\*L\_0$$

Then, we recurse by substituting the above expression for *B*<sub>1</sub> in the formula for *B*<sub>2</sub>:

$$L\_2 = \\frac{P\_2}{P\_1}\*\\frac{P\_1}{P\_0}\*L\_0$$
 Simplifying, we get:

$$L\_2 = \\frac{P\_2}{P\_0}\*L\_0$$
 And therefore by induction:

$$L\_n = \\frac{P\_n}{P\_0}\*L\_0$$

Which fulfills our design requirement articulated at the beginning, with one caveat -- this formula assumes that there have been no rebalances that have affected our balance.

Appendix C: *s*<sub>*n*</sub> in terms of *s*<sub>0</sub>
---------------------------------------------------------

From Appendix A,

$$s\_n = (2-\\frac{P\_n}{P\_{n-1}})S\_{n-1}$$
0 &lt; =*P*<sub>*n*</sub> &lt; =2*P*<sub>*n* − 1</sub>

If we convert this recursive formula into an iterative product begining with *s*<sub>0</sub>, it is:

$$s\_n = s\_0 \\prod\\limits\_{i=1}^n (2-\\frac{P\_n}{P\_{n-1}})$$

Example Scenario: Price Decreases Linearly to Zero
--------------------------------------------------

To get an idea of how this recursive function operates, suppose that between timestep 0 and timestep n, the price decreases linearly down to 0 from its starting price *P*<sub>0</sub>. We can insert this hypothetical simple example into the above formula:

$$s\_n = s\_0 \\prod\\limits\_{i=1}^n (2-\\frac{P\_{n-1} - \\frac{P\_0}{n}}{P\_{n-1}})$$
 and
$$s\_n = s\_0 \\prod\\limits\_{i=1}^n (1 + \\frac{P\_0}{nP\_{n-1}})$$

We can substitute in $P\_{n-1} = P\_0-(i-1)\\frac{P\_0}{n}$

$$s\_n = s\_0 \\prod\\limits\_{i=1}^n (1 + \\frac{P\_0}{n(P\_0-(i-1)\\frac{P\_0}{n})})$$

and

$$s\_n = s\_0 \\prod\\limits\_{i=1}^n (1 + \\frac{P\_0}{nP\_0-(i-1)P\_0)})$$

and

$$s\_n = s\_0 \\prod\\limits\_{i=1}^n (1 + \\frac{P\_0}{(n-i+1)P\_0})$$

and

$$s\_n = s\_0 \\prod\\limits\_{i=1}^n (1 + \\frac{1}{(n-i+1)})$$
$$ = s\_0 \\prod\\limits\_{i=1}^n (\\frac{n-i+2}{(n-i+1)})$$
 We can designate $s\_{i,n} to be:

$$s\_{n,i} = s\_0 \\prod\\limits\_{k=1}^i (\\frac{n-k+2}{(n-k+1)})$$
 Using factorials we can rewrite this as:

$$s\_{n,i} = s\_0 \\frac{\\frac{(n+1)!}{(n-i+1)!}}{\\frac{n!}{(n-i)!}}$$
 and

$$s\_{n,i} = s\_0 \\frac{(n+1)!(n-i)!}{n!(n-i+1)!}$$
 and

$$s\_{n,i} = s\_0 \\frac{(n+1)}{(n-i+1)}$$

Again in this toy example, how does the value function derived above compare with the more traditional short value function, with the same imaginary linear price curve from *P*<sub>0</sub> to 0 at timestep n. In that case *s*<sub>*n*</sub> = 2*S*<sub>0</sub>, and the values in between are linear interpolations between *s*<sub>0</sub> and *s*<sub>*n*</sub>.

So, the traditional value function is:

$$s\_{n,i} = s\_0(1 + \\frac{i}{n})$$

To compare this with the above derived axor short value function, we take the difference:

$$s\_0 \\frac{(n+1)}{(n-i+1)} - s\_0(1 + \\frac{i}{n})$$
$$ = s\_0(\\frac{(n+1)}{(n-i+1)} - \\frac{n+i}{n})$$
 To focus on the question of when this difference is negative or positive, we simplify the expression inside the parentheses, since *s*<sub>0</sub> is always positive:

$$ = \\frac{n+1}{n-i+1} - \\frac{(1+\\frac{i}{n})(n-i+1)}{n-i+1})$$
$$ = \\frac{n+1-(1+\\frac{i}{n})(n-i+1)}{n-i+1}$$
$$ = \\frac{n+1-(n-i+1 + i - \\frac{i^2}{n} + \\frac{i}{n})}{n-i+1}$$
$$ = \\frac{n+1-n+i-1 - i + \\frac{i^2}{n} - \\frac{i}{n}}{n-i+1}$$
$$ = \\frac{i^2 - i}{n(n-i+1)}$$
 Since n and i are defined as positive integers, with *n* ≥ *i*,

$$\\frac{i^2 - i}{n(n-i+1)} \\geqslant 0$$

Also, when *i* = 1, the difference between axor and traditional short value is 0. This means that after the first timestep they are exactly the same. However, when *i* &gt; 1, the difference is always positive, so when the price is going down in a linear way as in this scenario, it is always advantageous to hold a naxor when compared to a traditional short sale, after the first timestep.

Example Scenario: Price Doubles in Straight Line
------------------------------------------------

Arbitrary Price Movements
-------------------------

So we've shown that the axor short balance is always better than a traditional short balance whether the price goes up and down in a straight line, but is it possible to prove that we are better off than with a traditional short for non-linear, arbitrary price movements between *p*<sub>0</sub> and *p*<sub>*n*</sub>? In other words, is it possible for this expression to be less than 0?

$$s\_n = s\_0 \\prod\\limits\_{i=1}^n (2-\\frac{P\_n}{P\_{n-1}})$$

I haven't thought of a way to prove that analytically, but perhaps we can get a sense of the likelihood of this happening by doing a thousand monte-carlo simulations, where
