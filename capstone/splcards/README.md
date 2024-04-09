<h1>SPL Cards & Wallet Policy protocol</h1>
<h2>Renaissance hackathon submission</h2>
Program is deployed on devnet:
<code>6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew</code>

<hr />

Build project
<code>anchor build</code>

Start local Solana validator
<code>solana-test-validator</code>

Deploy program on local Solana validator
<code>anchor deploy</code>

A succesful deploy leads to the following message:
<code>Program Id: "your-program-id"</code>
<code>Deploy success</code>

Update the program id in two files:
<code>lib.rs</code>
<code>anchor.toml</code>

Build anchor program with the new program id:
<code>anchor build</code>

Test the program on your local Solana validator:
<code>anchor test --skip-local-validator</code>



